package com.atguigu.gmall.order.service.impl;

import java.math.BigDecimal;

import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.constant.MqConst;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.to.mq.OrderMsg;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.atguigu.gmall.model.activity.CouponInfo;

import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author SPF
 * @description 针对表【order_info(订单表 订单表)】的数据库操作Service实现
 * @createDate 2022-09-13 21:25:54
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
        implements OrderInfoService {

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderDetailService orderDetailService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 根据页面提交的数据生成一个数据库的订单
     *
     * @param submitVo
     * @return
     */

    // 数据库成功  + 消息成功
    @Transactional
    @Override
    public Long saveOrder(OrderSubmitVo submitVo, String tradeNo) {

        //1.准备订单数据
        OrderInfo orderInfo = prepareOrderInfo(submitVo, tradeNo);

        ///2、保存-OrderInfo
        orderInfoMapper.insert(orderInfo);

        //2、保存-OrderDetail
        //订单明细。指这个订单到底买了那些商品？
        //所有订单明细
        List<OrderDetail> details = prepareOrderDetail(submitVo, orderInfo);
        orderDetailService.saveBatch(details);

        OrderMsg orderMsg = new OrderMsg(orderInfo.getId(),orderInfo.getUserId());
        rabbitTemplate.convertAndSend(MqConst.EXCHANGE_ORDER_EVNT,MqConst.RK_ORDER_CREATED, Jsons.toStr(orderMsg));

        //3.返回订单id
        return orderInfo.getId();
    }

    @Override
    public void changeOrderStatus(Long orderId, Long userId, ProcessStatus closed, List<ProcessStatus> expected) {


        String orderStatus = closed.getOrderStatus().name(); //订单状态
        String processStatus = closed.name(); //处理状态

        List<String> expects = expected.stream().map(status -> status.name()).collect(Collectors.toList());

        //幂等修改订单
        orderInfoMapper.updateOrderStatus(orderId,userId,processStatus,orderStatus,expects);

    }

    /**
     * 订单明细
     *
     * @param submitVo
     * @param orderInfo
     * @return
     */
    private List<OrderDetail> prepareOrderDetail(OrderSubmitVo submitVo, OrderInfo orderInfo) {

        List<OrderDetail> detailList = submitVo.getOrderDetailList().stream()
                .map(vo -> {
                    OrderDetail detail = new OrderDetail();
                    detail.setOrderId(orderInfo.getId());
                    detail.setSkuId(vo.getSkuId());
                    detail.setUserId(orderInfo.getUserId());
                    detail.setSkuName(vo.getSkuName());
                    detail.setImgUrl(vo.getImgUrl());
                    detail.setOrderPrice(vo.getOrderPrice());
                    detail.setSkuNum(vo.getSkuNum());
                    detail.setHasStock(vo.getHasStock());
                    detail.setCreateTime(new Date());
                    detail.setSplitTotalAmount(vo.getOrderPrice().multiply(new BigDecimal(vo.getSkuNum() +"")));
                    detail.setSplitActivityAmount(new BigDecimal("0"));
                    detail.setSplitCouponAmount(new BigDecimal("0"));
                    detail.setId(0L);
                    return detail;
                }).collect(Collectors.toList());

        return detailList;
    }

    private OrderInfo prepareOrderInfo(OrderSubmitVo submitVo, String tradeNo) {

        OrderInfo info = new OrderInfo();

        //收货人
        info.setConsignee(submitVo.getConsignee());
        info.setConsigneeTel(submitVo.getConsigneeTel());

        //计算总价
        BigDecimal totalAmount = submitVo.getOrderDetailList().stream()
                .map(o -> o.getOrderPrice().multiply(new BigDecimal(o.getSkuNum() + "")))
                .reduce((o1, o2) -> o1.add(o2)).get();

        //订单金额
        info.setTotalAmount(totalAmount);

        //订单状态(默认未支付)
        info.setOrderStatus(OrderStatus.UNPAID.name());

        //用户id
        Long userId = AuthUtils.getCurrentAuthInfo().getUserId();
        info.setUserId(userId);

        //支付方式
        info.setPaymentWay(submitVo.getPaymentWay());

        //配送地址
        info.setDeliveryAddress(submitVo.getDeliveryAddress());
        //订单备注
        info.setOrderComment(submitVo.getOrderComment());

        //订单流水号
        info.setOutTradeNo(tradeNo);

        //交易体  拿到这个订单中购买的第一个商品的名字，作为订单的体
        info.setTradeBody(submitVo.getOrderDetailList().get(0).getSkuName());

        //订单创建时间
        info.setCreateTime(new Date());

        //订单过期时间  订单多久没支付以后就过期，过期未支付，订单就会成为已关闭状态
        info.setExpireTime(new Date(System.currentTimeMillis() + 1000 * SysRedisConst.ORDER_CLOSE_TTL));

        //订单的处理状态
        info.setProcessStatus(ProcessStatus.UNPAID.name());

        //物流单编号，发货后才有
        info.setTrackingNo("");
        //父订单id（拆单场景）
        info.setParentOrderId(0L);

        //订单的图片
        info.setImgUrl(submitVo.getOrderDetailList().get(0).getImgUrl());


        //订单明细
//        List<OrderDetail> orderDetails = submitVo.getOrderDetailList().stream()
//                .map(cartInfoVo -> {
//                    OrderDetail detail = new OrderDetail();
//
//                    return detail;
//                }).collect(Collectors.toList());
//        info.setOrderDetailList(orderDetails);

        //当前单被优惠活动减掉的金额
        info.setActivityReduceAmount(new BigDecimal("0"));
        //当前单被优惠券减掉的金额
        info.setCouponAmount(new BigDecimal("0"));

        //订单原始总额
        info.setOriginalTotalAmount(totalAmount);

        //可退款的最后时间
        info.setRefundableTime(new Date(System.currentTimeMillis() + SysRedisConst.ORDER_REFUND_TTL * 1000));

        //运费。第三方物流平台，动态计算运费
        info.setFeightFee(new BigDecimal("0"));
        info.setOperateTime(new Date());


        return info;
    }
}




