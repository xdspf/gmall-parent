package com.atguigu.gmall.pay.service;

import com.alipay.api.AlipayApiException;

import java.util.Map;

public interface AlipayService {
    String getAlipayPageHtml(Long orderId) throws AlipayApiException;

    boolean rsaCheckV1(Map<String, String> paramMaps) throws AlipayApiException;

    /*
            发送支付成功消息给订单交换机
     */
    void sendPayedMsg(Map<String, String> param);
}
