package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadPoolExecutor;

@RestController
public class ThreadPoolController {


    @Autowired
    ThreadPoolExecutor executor;

    //关闭线程池
    @GetMapping("/close/pool")
    public Result closePool(){
        executor.shutdown();
        return Result.ok();
    }

    //监控线程池

    @GetMapping("/monitor/pool")
    public Result monitorThreadPool(){
        int corePoolSize = executor.getCorePoolSize();
        long taskCount = executor.getTaskCount();

        return Result.ok(corePoolSize + "----" +taskCount);
    }


    }
