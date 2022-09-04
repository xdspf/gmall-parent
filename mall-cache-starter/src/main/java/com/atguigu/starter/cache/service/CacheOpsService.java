package com.atguigu.starter.cache.service;


import java.lang.reflect.Type;

public interface CacheOpsService {
//    SkuDetailTo getCacheDate(String cacheKey, Class<SkuDetailTo> skuDetailToClass);


    /*
        从缓存中获取一个json并转为普通对象
     */

    <T>T  getCacheData(String cacheKey, Class<T> clz);

     /*
        从缓存中获取一个json并转为复杂对象
     */

    Object  getCacheData(String cacheKey, Type type);

        /**
         * 布隆过滤器判断是否有这个商品
         *
         * @param skuId*/
    boolean bloomContains(Object skuId);


    /*
        判断指定布隆过滤器(bloomName) 是否包含指定值(bVal)
     */

    boolean bloomContains(String bloomName, Object bVal);


    /**
     * 给指定商品加锁
     * @param skuId
     * @return
     */
    boolean tryLock(Long skuId);

    /*
        加指定的锁
     */
    boolean tryLock(String lockName);

    /**
     * 把指定对象使用指定的key保存到redis
     * @param cacheKey
     * @param fromRpc
     */
    void saveData(String cacheKey, Object fromRpc);


    void saveData(String cacheKey, Object fromRpc,Long dataTtl);

    /**
     * 解锁
     * @param skuId
     */
    void unlock(Long skuId);

    /**
     * 解指定的锁
     * @param lockName
     */

    void unlock(String lockName);


    /*
        延迟双删
     */
    void delay2Delete(String cacheKey);

}
