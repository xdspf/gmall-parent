package com.atguigu.gmall.web;

import com.atguigu.gmall.common.annotation.EnableAutoFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableAutoFeignInterceptor
@SpringCloudApplication //把配置放在application.yaml中
@EnableFeignClients(basePackages = {"com.atguigu.gmall.feign.item",
                        "com.atguigu.gmall.feign.product",
                        "com.atguigu.gmall.feign.search",
        "com.atguigu.gmall.feign.cart"})  //开启远程调用功能
//方式一
//@EnableDiscoveryClient //把服务信息暴露给消费端
//@EnableCircuitBreaker
//@SpringBootApplication(exclude = DataSourceAutoConfiguration.class) //排出数据源的自动配置（DataSourceAutoConfiguration）
public class WebAllMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAllMainApplication.class, args);
    }
}
