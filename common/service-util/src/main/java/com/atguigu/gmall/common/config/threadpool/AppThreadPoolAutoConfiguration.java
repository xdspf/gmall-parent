package com.atguigu.gmall.common.config.threadpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//开启自动化属性绑定配置
@EnableConfigurationProperties(AppThreadPoolProperties.class)  //AppThreadPoolProperties组件自动放到容器中
@Configuration
public class AppThreadPoolAutoConfiguration {


    /**
     * int corePoolSize,  核心线程池： cpu核心数   4
     * int maximumPoolSize, 最大线程数：          8
     * long keepAliveTime,  线程存活时间
     * TimeUnit unit,      时间单位
     * BlockingQueue<Runnable> workQueue, 阻塞队列：大小需要合理
     * ThreadFactory threadFactory,  线程工厂。 自定义创建线程的方法
     * RejectedExecutionHandler handler  拒绝策略
     * <p>
     * //  2000/s：队列大小根据接口吞吐量标准调整
     */

    @Autowired
    AppThreadPoolProperties threadPoolProperties;

    @Value("${spring.application.name}")
    String applicationName;


    @Bean
    public ThreadPoolExecutor coreExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                threadPoolProperties.getCore(),
                threadPoolProperties.getMax(),
                threadPoolProperties.getKeepAliveTime(), TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(threadPoolProperties.getQueueSize()),
                new ThreadFactory() { //复制给线程池创建线程
                    int i = 0; //记录线程自增id

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName(applicationName+"[core-thread-"+ i++ +"]");
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        return executor;
    }
}
