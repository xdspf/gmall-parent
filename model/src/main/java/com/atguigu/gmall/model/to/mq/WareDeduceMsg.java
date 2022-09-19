package com.atguigu.gmall.model.to.mq;

import lombok.Data;

import java.util.List;

@Data
public class WareDeduceMsg {

    Long orderId;
    String consignee;
    String consigneeTel;
    String orderComment; //订单备注
    String orderBody; //订单概要
    String deliveryAddress;  //发货地址
    String paymentWay = "2";  //支付方式  ： 1  货到付款  2 在线支付
    List<WareDeduceSkuInfo> details;  //购买商品明细

}
