package com.atguigu.gmall.item.cache;


import com.atguigu.gmall.model.to.SkuDetailTo;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import org.redisson.api.RedissonClient;

public interface CacheOpsService {
//    SkuDetailTo getCacheDate(String cacheKey, Class<SkuDetailTo> skuDetailToClass);


    <T>T  getCacheDate(String cacheKey, Class<T> clz);

        /**
         * 布隆过滤器判断是否有这个商品
         **/
    boolean bloomContains(Long skuId);


    /**
     * 给指定商品加锁
     * @param skuId
     * @return
     */
    boolean tryLock(Long skuId);

    /**
     * 把指定对象使用指定的key保存到redis
     * @param cacheKey
     * @param fromRpc
     */
    void saveData(String cacheKey, Object fromRpc);

    /**
     * 解锁
     * @param skuId
     */
    void unlock(Long skuId);
}
