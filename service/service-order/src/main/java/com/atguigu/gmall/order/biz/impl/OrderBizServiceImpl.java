package com.atguigu.gmall.order.biz.impl;

import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.constant.MqConst;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.feign.product.SkuProductFeignClient;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.feign.ware.WareFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.to.mq.OrderMsg;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.order.CartInfoVo;
import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.service.OrderInfoService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrderBizServiceImpl implements OrderBizService {

    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    SkuProductFeignClient productFeignClient;

    @Autowired
    WareFeignClient wareFeignClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public OrderConfirmDataVo getConfirmData() {
        OrderConfirmDataVo vo = new OrderConfirmDataVo();
        //1.?????????????????????????????????
        //???????????????????????????
        //?????????????????????????????????redis????????????????????????????????????????????????
        List<CartInfo> data = cartFeignClient.getChecked().getData();

        List<CartInfoVo> infoVos = data.stream()
                .map(cartInfo -> {
                    CartInfoVo infoVo = new CartInfoVo();
                    infoVo.setSkuId(cartInfo.getSkuId());
                    infoVo.setImgUrl(cartInfo.getImgUrl());
                    infoVo.setSkuName(cartInfo.getSkuName());

                    //??????????????????
                    Result<BigDecimal> price = productFeignClient.getSku1010Price(cartInfo.getSkuId());
                    infoVo.setOrderPrice(price.getData());
                    infoVo.setSkuNum(cartInfo.getSkuNum());

                    String stock = wareFeignClient.hasStock(cartInfo.getSkuId(), cartInfo.getSkuNum());
                    infoVo.setHasStock(stock);

                    return infoVo;
                }).collect(Collectors.toList());


        vo.setDetailArrayList(infoVos);

        //2.????????????????????????
        Integer integer = infoVos.stream().map(CartInfoVo::getSkuNum).reduce((o1, o2) -> o1 + o2).get();
        vo.setTotalNum(integer);

        //3.?????????
        BigDecimal totalAmount = infoVos.stream()
                .map(cartInfo -> cartInfo.getOrderPrice().multiply(new BigDecimal(cartInfo.getSkuNum() + "")))
                .reduce((o1, o2) -> o1.add(o2)).get();

        vo.setTotalAmount(totalAmount);

        //4.??????????????????????????????
        Result<List<UserAddress>> addressList = userFeignClient.getUserAddressList();
        vo.setUserAddressList(addressList.getData());

        //5.?????????????????????
        String tradeNo = generateTradeNo();
        //???????????????
        vo.setTradeNo(tradeNo);


        return vo;
    }

    @Override
    public String generateTradeNo() {
        long millis = System.currentTimeMillis();
        UserAuthInfo info = AuthUtils.getCurrentAuthInfo(); //????????????id
        String tradeNo = millis + "_" + info.getUserId();

        //redis??????????????????
        redisTemplate.opsForValue().set(SysRedisConst.ORDER_TEMP_TOKEN + tradeNo, "1", 15, TimeUnit.MINUTES);
        return tradeNo;
    }

    /**
     * ????????????
     */

    @Override
    public boolean checkTradeNo(String tradeNo) {

        //1????????????????????????????????????????????????, 1, 0 ?????????????????????
        String lua = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then " +
                "    return redis.call(\"del\",KEYS[1]) " +
                "else " +
                "    return 0 " +
                "end";
        Long execute = redisTemplate.execute(new DefaultRedisScript<Long>(lua, Long.class),
                Arrays.asList(SysRedisConst.ORDER_TEMP_TOKEN + tradeNo), new String[]{"1"});

        if (execute > 0) {
            //?????????????????????????????????
            return true;
        }
        return false;


//        String val = redisTemplate.opsForValue().get(SysRedisConst.ORDER_TEMP_TOKEN + tradeNo);
//        if(!StringUtils.isEmpty(val)){
//            //redis??????????????????????????????
//            redisTemplate.delete(SysRedisConst.ORDER_TEMP_TOKEN + tradeNo);
//            return true;
//        }


    }


    @Override
    public Long submitOrder(OrderSubmitVo submitVo, String tradeNo) {

        //1.?????????
        boolean checkTradeNo = checkTradeNo(tradeNo);
        if (!checkTradeNo) {
            throw new GmallException(ResultCodeEnum.TOKEN_INVAILD);
        }

        //2.?????????
        List<String> noStockSkus = new ArrayList<>();
        for (CartInfoVo infoVo : submitVo.getOrderDetailList()) {
            Long skuId = infoVo.getSkuId();
            String stock = wareFeignClient.hasStock(skuId, infoVo.getSkuNum());

            if (!"1".equals(stock)) {
                noStockSkus.add(infoVo.getSkuName());
            }

        }

        if (noStockSkus.size() > 0) {
            GmallException exception = new GmallException(ResultCodeEnum.ORDER_NO_STOCK);
            String skuNames = noStockSkus.stream().reduce((s1, s2) -> s1 + " " + s2).get();
            throw new GmallException(ResultCodeEnum.ORDER_NO_STOCK.getMessage() + skuNames, ResultCodeEnum.ORDER_NO_STOCK.getCode());
        }

        //3.?????????
        List<String> skuNames = new ArrayList<>();
        for (CartInfoVo infoVo : submitVo.getOrderDetailList()) {

            Result<BigDecimal> price = productFeignClient.getSku1010Price(infoVo.getSkuId());
            if (!price.getData().equals(infoVo.getOrderPrice())) {
                skuNames.add(infoVo.getSkuName());
            }
        }

        if (skuNames.size() > 0) {
            String skuName = skuNames.stream().reduce((s1, s2) -> s1 + " " + s2).get();
            throw new GmallException(ResultCodeEnum.ORDER_PRICE_CHANGED.getMessage() + "<br/>" + skuName, ResultCodeEnum.ORDER_PRICE_CHANGED.getCode());
        }

        //4.?????????????????????????????????
        Long orderId = orderInfoService.saveOrder(submitVo, tradeNo);

        //5.?????????????????????????????????
        cartFeignClient.deleteChecked();

        //45min????????????????????????
        //???MQ??????????????????????????????????????????????????????
        //??????????????????????????????????????????????????????


        //45min????????????????????????
//        ScheduledExecutorService pool = Executors.newScheduledThreadPool(10);
//        pool.schedule(()->{
//            closeOrder(orderId);
//        },45,TimeUnit.MINUTES);
//
        return orderId;
    }


    @Override
    public void closeOrder(Long orderId, Long userId) {
        ProcessStatus closed = ProcessStatus.CLOSED;
        List<ProcessStatus> expected = Arrays.asList(ProcessStatus.UNPAID, ProcessStatus.FINISHED);

        //?????????????????????????????????????????????????????? CAS
        orderInfoService.changeOrderStatus(orderId, userId, closed, expected);


    }

//    @Scheduled(cron = "0 */5 * * * ?")
//    public void closeOrder(Long orderId){
//
//    }

}
