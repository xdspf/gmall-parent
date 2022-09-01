package com.atguigu.gmall.item.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 使用Redis实现的分布式锁
 */

@Service
public class RedisDistLock {

    @Autowired
    StringRedisTemplate redisTemplate;


    /*
        加锁的方法
     */
    public String lock(){

        String token = UUID.randomUUID().toString();
        //1.加锁
        //2.设置过期时间
        while ( !redisTemplate.opsForValue().setIfAbsent("lock", token, 10, TimeUnit.SECONDS) ){
            //自旋阻塞式加锁
        }
        return token;

    }
    /*
    减锁
     */

    public void unlock(String token){
        String luaScript = "if redis.call('get',KEYS[1]) == ARGV[1]  then return redis.call('del',KEYS[1]); else  return 0;end;";
        //执行脚本
        redisTemplate.execute(new DefaultRedisScript<>(luaScript, Long.class), Arrays.asList("lock"), token);
    }

}
