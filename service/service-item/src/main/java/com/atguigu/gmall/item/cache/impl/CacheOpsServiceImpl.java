package com.atguigu.gmall.item.cache.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.cache.CacheOpsService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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

     /*
    从缓存中获取一个数据，并转成指定类型的对象
     */

    @Override
    public <T> T getCacheDate(String cacheKey, Class<T> clz) {

        String jsonStr = redisTemplate.opsForValue().get(cacheKey);

        //引入null值缓存机制
        if (SysRedisConst.NULL_VAL.equals(jsonStr)) {
            return null;
        }

        T t = Jsons.toObj(jsonStr, clz);//将json转为指定的类型

        return t;
    }


    @Override
    public boolean bloomContains(Long skuId) {
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);

        return filter.contains(skuId);
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
    /*
        减锁
     */

    @Override
    public void unlock(Long skuId) {

        String lockKey = SysRedisConst.LOCK_SKU_DETAIL + skuId;
        RLock lock = redissonClient.getLock(lockKey);
        lock.unlock();
    }


}
