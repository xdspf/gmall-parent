package com.atguigu.gmall.pay.config;


import com.alipay.api.AlipayConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.alipay")
public class AlipayProperties {

    private String gatewayUrl;

    private String appId;

    private String merchantPrivateKey;  //商户私钥
    private String charset;   //字符编码
    private String alipayPublicKey;  //支付公钥
    private String signType;  //签名方式

    private String returnUrl;  //同步通知地址
    private String notifyUrl;  //异步通知地址

}
