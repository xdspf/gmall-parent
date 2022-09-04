package com.atguigu.starter.cache.service.impl;


import com.atguigu.starter.cache.constant.SysRedisConst;
import com.atguigu.starter.cache.service.CacheOpsService;
import com.atguigu.starter.cache.utils.Jsons;
import com.fasterxml.jackson.core.type.TypeReference;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*

 */

@Service
public class CacheOpsServiceImpl implements CacheOpsService {

    //缓存
    @Autowired
    StringRedisTemplate redisTemplate;


    //加锁
    @Autowired
    RedissonClient redissonClient;

    //专门执行延迟任务的线程池   （4个线程）
    ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(4);

     /*
    从缓存中获取一个数据，并转成指定类型的对象
     */

    @Override
    public <T> T getCacheData(String cacheKey, Class<T> clz) {

        String jsonStr = redisTemplate.opsForValue().get(cacheKey);

        //引入null值缓存机制
        if (SysRedisConst.NULL_VAL.equals(jsonStr)) {
            return null;
        }

        T t = Jsons.toObj(jsonStr, clz);//将json转为指定的类型

        return t;
    }

    @Override
    public Object getCacheData(String cacheKey, Type type) {
        String jsonStr = redisTemplate.opsForValue().get(cacheKey);

        //引入null值缓存机制
        if (SysRedisConst.NULL_VAL.equals(jsonStr)) {
            return null;
        }

        //逆转json为Type类型的复杂对象
        Object obj = Jsons.toObj(jsonStr, new TypeReference<Object>() {
            @Override
            public Type getType() {

                return type; //这个是方法的带泛型的返回值类型
            }
        });//将json转为指定的类型
        return obj;
    }


    @Override
    public boolean bloomContains(Object skuId) {
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);

        return filter.contains(skuId);
    }

    @Override
    public boolean bloomContains(String bloomName, Object bVal) {
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(bloomName);
        return filter.contains(bVal);
    }

    @Override
    public boolean tryLock(Long skuId) {
        //1.准备锁常用的key  lock:sku:detail:49
        String lockKey = SysRedisConst.LOCK_SKU_DETAIL + skuId;
        RLock lock = redissonClient.getLock(lockKey);
        //2.加锁
        boolean tryLock = lock.tryLock();
        return tryLock;
    }

    @Override
    public boolean tryLock(String lockName) {
        RLock rLock = redissonClient.getLock(lockName);
        return rLock.tryLock();

    }

    @Override
    public void saveData(String cacheKey, Object fromRpc) {
        if (fromRpc == null) {
            //null值缓存短一点时间
            redisTemplate.opsForValue().set(cacheKey, SysRedisConst.NULL_VAL, SysRedisConst.NULL_VAL_TTL, TimeUnit.SECONDS);
        } else {
            //有值，缓存时间设置久一点
            String str = Jsons.toStr(fromRpc);
            redisTemplate.opsForValue().set(cacheKey, str, SysRedisConst.SKUDETAIL_VAL_TTL, TimeUnit.SECONDS);
        }
    }

    @Override
    public void saveData(String cacheKey, Object fromRpc, Long dataTtl) {

        if (fromRpc == null) {
            //null值缓存短一点时间
            redisTemplate.opsForValue().set(cacheKey, SysRedisConst.NULL_VAL, SysRedisConst.NULL_VAL_TTL, TimeUnit.SECONDS);
        } else {
            //有值，缓存时间设置久一点
            String str = Jsons.toStr(fromRpc);
            redisTemplate.opsForValue().set(cacheKey, str, dataTtl, TimeUnit.SECONDS);
        }

    }
    /*
        减锁
     */

    @Override
    public void unlock(Long skuId) {

        String lockKey = SysRedisConst.LOCK_SKU_DETAIL + skuId;
        RLock lock = redissonClient.getLock(lockKey);
        lock.unlock();
    }

    @Override
    public void unlock(String lockName) {
        RLock lock = redissonClient.getLock(lockName);
        lock.unlock(); //redisson防止删除别人的锁
    }

    @Override
    public void delay2Delete(String cacheKey) {
        redisTemplate.delete(cacheKey);

        //1.提交一个延时任务（再次删除缓存），断电失效  结合后台管理系统，专门准备清空缓存的按钮功能
        //2.分布式池框架  Redisson
        scheduledExecutor.schedule(()->{
            redisTemplate.delete(cacheKey);
        },5,TimeUnit.SECONDS);


    }


}
