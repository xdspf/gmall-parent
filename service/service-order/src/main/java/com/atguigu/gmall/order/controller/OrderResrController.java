package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.biz.OrderBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/order/auth")   //带auth的需要认证登录
@RestController
public class OrderResrController {


    @Autowired
    OrderBizService orderBizService;

    /**
     * 提交订单
     *
     * @return
     */

    @PostMapping("/submitOrder")
    public Result submitOrder(@RequestParam("tradeNo") String tradeNo, @RequestBody OrderSubmitVo submitVo) {
        //提交订单
        Long orderId = orderBizService.submitOrder(submitVo,tradeNo);

        return Result.ok(orderId.toString());
    }

}
