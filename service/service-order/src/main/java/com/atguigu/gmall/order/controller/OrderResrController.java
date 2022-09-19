package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/order/auth")   //带auth的需要认证登录
@RestController
public class OrderResrController {


    @Autowired
    OrderBizService orderBizService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    OrderDetailService orderDetailService;


    /**
     * 提交订单
     *
     * @return
     */

    @PostMapping("/submitOrder")
    public Result submitOrder(@RequestParam("tradeNo") String tradeNo, @RequestBody OrderSubmitVo submitVo) {
        //提交订单
        Long orderId = orderBizService.submitOrder(submitVo, tradeNo);


        return Result.ok(orderId.toString());
    }

    //查看订单列表   http://api.gmall.com/api/order/auth/1/10


    @GetMapping("/{pn}/{ps}")
    public Result orderList(@PathVariable("pn") Long pn, @PathVariable("ps") Long ps) {

        Long userId = AuthUtils.getCurrentAuthInfo().getUserId();

        Page<OrderInfo> page = new Page<>(pn, ps);
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getUserId, userId);

        //1.查询orderInfo
        Page<OrderInfo> infoPage = orderInfoService.page(page, queryWrapper);

        //2.查询orderInfo的所有商品
        infoPage.getRecords().stream().parallel().forEach(orderInfo -> {
            //查询订单详情
            List<OrderDetail> orderDetails = orderDetailService.getOrderDetails(orderInfo.getId(), orderInfo.getUserId());
            orderInfo.setOrderDetailList(orderDetails);

        });

        return Result.ok(infoPage);
    }

}
