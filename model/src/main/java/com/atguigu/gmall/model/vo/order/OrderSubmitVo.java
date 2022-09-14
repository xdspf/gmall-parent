package com.atguigu.gmall.model.vo.order;

import lombok.Data;

import java.util.List;

/**
 * 订单提交 数据模型
 */
@Data
public class OrderSubmitVo {
    private String consignee; // 收货人
    private String consigneeTel;//收货人电话
    private String deliveryAddress;  //收货人地址
    private String paymentWay;  //支付方式
    private String orderComment;  //订单的备注
    private List<CartInfoVo> orderDetailList;  //CartInfoVo
}
