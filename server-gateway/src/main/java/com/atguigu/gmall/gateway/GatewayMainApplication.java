package com.atguigu.gmall.gateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
/*
    主启动类
 */

//@SpringBootApplication
//@EnableDiscoveryClient   //开启服务发现
//@EnableCircuitBreaker   //开启服务熔断降级（1.导入sentinel.jar  2.使用这个注解）

@SpringCloudApplication   //以上三个的合体
public class GatewayMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayMainApplication.class,args);
    }
}
