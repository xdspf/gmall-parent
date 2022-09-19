package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.payment.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
* @author SPF
* @description 针对表【payment_info(支付信息表)】的数据库操作Service
* @createDate 2022-09-13 21:25:54
*/
public interface PaymentInfoService extends IService<PaymentInfo> {

    PaymentInfo savePaymentInfo(Map<String,String> map);
}
