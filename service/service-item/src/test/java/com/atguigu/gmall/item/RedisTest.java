package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class RedisTest {

    //自动注入SpringBoot提供的工具类
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void test1(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello","world");
        System.out.println("redis保存完成");

        String hello = ops.get("hello");
        System.out.println("获取到的key:" +hello);

    }
}
