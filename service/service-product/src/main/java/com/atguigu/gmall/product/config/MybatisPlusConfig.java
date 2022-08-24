package com.atguigu.gmall.product.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration   //配置类
public class MybatisPlusConfig {

    @Bean  //放到容器中
    public MybatisPlusInterceptor interceptor() {

        //插件主体
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        //加入内部的小插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setOverflow(true);//溢出总页数后是否进行处理

        //分页插件
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        return interceptor;

    }

}
