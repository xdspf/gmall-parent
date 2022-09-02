package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.feign.SkuDetailFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.starter.cache.annotation.GmallCache;
import com.atguigu.starter.cache.service.CacheOpsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    private Map<Long, SkuDetailTo> skuCache = new ConcurrentHashMap<>(); //因为并发有场景，因此用ConcurrentHashMap

    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    CacheOpsService cacheOpsService;

    //每个skuId，关联一把自己的锁
    Map<Long, ReentrantLock> lockPool = new ConcurrentHashMap<>();
    ReentrantLock lock = new ReentrantLock(); //锁得住


    /**
     * 表达式中的params代表方法的所有参数列表
     * @param skuId
     * @return
     */

//    @Transactional
    @GmallCache(cacheKey = SysRedisConst.SKU_INFO_PREFIX +"#{#params[0]}",
                bloomName = SysRedisConst.BLOOM_SKUID,
                bloomValue = "#{#params[0]}",
                lockName = SysRedisConst.LOCK_SKU_DETAIL + "#{#params[0]}"
    )
    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        SkuDetailTo fromRpc = getSkuDetailFromRpc(skuId);
        return fromRpc;
    }




//    @Override
//    public SkuDetailTo getSkuDetail(Long skuId) {
//        Result<SkuDetailTo> skuDetail = skuDetailFeignClient.getSkuDetail(skuId);
//
//        return skuDetail.getData();
//    }




   /* @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        String cacheKey = SysRedisConst.SKU_INFO_PREFIX + skuId;
        //1.查看缓存(从缓存中获取一个数据，并转为这个类型)
        SkuDetailTo cacheData = cacheOpsService.getCacheDate(cacheKey, SkuDetailTo.class);
        //2.判断
        if (cacheData == null){
            //3.缓存没有
            //4.先问布隆，是否有这个商品
          boolean contain =   cacheOpsService.bloomContains(skuId);
          if (!contain){
              //5.布隆说没有，一定没有
              log.info("{} 商品--布隆判定没有，检测到隐藏的攻击风险。。。。");
              return null;
          }
          //6、布隆说有，有可能有，就需要回源查数据
            //为当前商品加自己的分布式锁。100w的49号查询只会放进一个
            boolean lock = cacheOpsService.tryLock(skuId);
          if (lock){
              //7、获取锁成功，查询远程
              log.info("[{}缓存未命中,布隆说有，准备回源。。。。。。。。。。。。",skuId);
              SkuDetailTo fromRpc = getSkuDetailFromRpc(skuId);
              //8.数据放缓存
              cacheOpsService.saveData(cacheKey,fromRpc);
              //9.解锁
              cacheOpsService.unlock(skuId);
              return fromRpc;
          }

          //10.没有获取到锁
            try {
                Thread.sleep(1000);
               return cacheOpsService.getCacheDate(cacheKey, SkuDetailTo.class);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //4.缓存中有，直接返回
        return cacheData;
    }
*/

    /**
     * 进行 redis分布式缓存优化
     */


   /* @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        lockPool.put(skuId,new ReentrantLock());
        //1、看缓存中有没有  sku:info:50
        String jsonStr = redisTemplate.opsForValue().get("sku:info:" + skuId);
        if ("x".equals(jsonStr)){
            //说明以前查过，只不过数据库没有此记录，为了避免再次回源，缓存了一个占位符
            return null;
        }
        if (StringUtils.isEmpty(jsonStr)) {
            //2.redis没有缓存
            //2.1回源  之前可以判断redis中保存的sku的id集合，有没有这个id
            SkuDetailTo fromRpc = null;
            //防止随机值穿透攻击？ 回源之前，先要用布隆/bitmap判断有没有
//            ReentrantLock lock = new ReentrantLock();  锁不住

            //判断锁池中是否有自己的锁
            //锁池中不存在就放一把新的锁，作为自己的锁，存在就用之前的锁
            ReentrantLock lock = lockPool.putIfAbsent(skuId, new ReentrantLock());

            boolean b = this.lock.tryLock(); //立即尝试加锁，不用等，瞬发。等待逻辑在业务上 .抢一下，不成就不用再抢了
//            boolean b = lock.tryLock(1, TimeUnit.SECONDS); //等待逻辑在锁上.1s内，CPU疯狂抢锁
            if (b){
                //抢到锁
                fromRpc = getSkuDetailFromRpc(skuId);
            }else {
                //没抢到
//                Thread.sleep(1000);
                jsonStr = redisTemplate.opsForValue().get("sku:info:" + skuId);

                return null;

            }



            //2.2、放入缓存【查到的对象转为json字符串保存到redis】
            String cacheJson = "x";
            if (fromRpc != null){
                cacheJson = Jsons.toStr(fromRpc);
                redisTemplate.opsForValue().set("sku:info:" + skuId, cacheJson,7, TimeUnit.DAYS);
            }else {
                redisTemplate.opsForValue().set("sku:info:" + skuId, cacheJson,30, TimeUnit.MINUTES);
            }
            return fromRpc;
        }
        //3、缓存中有. 把json转成指定的对象
        SkuDetailTo skuDetailTo = Jsons.toObj(jsonStr, SkuDetailTo.class);

        return skuDetailTo;
    }
*/

    /**
     * 进行 本地缓存优化
     */
   /* @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        //先看缓存
        SkuDetailTo cacheData = skuCache.get(skuId);

        //判断缓存中是否有数据
        if (cacheData == null) {
            SkuDetailTo fromRpc = getSkuDetailFromRpc(skuId);
            skuCache.put(skuId,fromRpc);
            return fromRpc;
        }
        //缓存有，直接返回
        return cacheData;
    }
*/


    /*
            异步编排的优化（未进行缓存优化）
     */
    public SkuDetailTo getSkuDetailFromRpc(Long skuId) {
        SkuDetailTo detailTo = new SkuDetailTo();

        CompletableFuture<SkuInfo> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            Result<SkuInfo> result = skuDetailFeignClient.getSkuInfo(skuId);
            //1.查基本信息
            SkuInfo skuInfo = result.getData();
            detailTo.setSkuInfo(skuInfo);
            return skuInfo;
        }, executor);


        CompletableFuture<Void> imageFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {

            if (skuInfo != null){ //判断是否为null

                //2.查商品图片信息
                Result<List<SkuImage>> skuImage = skuDetailFeignClient.getSkuImage(skuId);
                skuInfo.setSkuImageList(skuImage.getData());
            }
        }, executor);

        CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> {

            //3.查商品实时价格
            Result<BigDecimal> sku1010Price = skuDetailFeignClient.getSku1010Price(skuId);
            detailTo.setPrice(sku1010Price.getData());
        }, executor);


        CompletableFuture<Void> saleAttrFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo != null){

                Long spuId = skuInfo.getSpuId();

                //4.查销售属性名和值
                Result<List<SpuSaleAttr>> saleattrvalues = skuDetailFeignClient.getSkuSaleattrvalues(skuId, spuId);
                detailTo.setSpuSaleAttrList(saleattrvalues.getData());
            }
        }, executor);

        CompletableFuture<Void> skuValueFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo != null){

                //5.sku组合
                Result<String> skuValueJson = skuDetailFeignClient.getSkuValueJson(skuInfo.getSpuId());
                detailTo.setValuesSkuJson(skuValueJson.getData());
            }
        }, executor);

        CompletableFuture<Void> categoryFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo != null){

                //6.查分类
                Result<CategoryViewTo> categoryview = skuDetailFeignClient.getSkuCategoryview(skuInfo.getCategory3Id());
                detailTo.setCategoryView(categoryview.getData());
            }
        }, executor);

        CompletableFuture.allOf(imageFuture, priceFuture, saleAttrFuture, skuValueFuture, categoryFuture)
                .join();

        return detailTo;
    }



}
