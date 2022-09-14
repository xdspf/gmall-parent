package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author SPF
* @description 针对表【order_info(订单表 订单表)】的数据库操作Service
* @createDate 2022-09-13 21:25:54
*/
public interface OrderInfoService extends IService<OrderInfo> {

    Long saveOrder(OrderSubmitVo submitVo,String tradeNo);
}
