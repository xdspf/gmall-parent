package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@RequestMapping("/api/inner/rpc/product")
@FeignClient("service-product")
public interface SkuDetailFeignClient {

//    @GetMapping("/skudetail/{skuId}")
//    public Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId") Long skuId);

    /*
    优化
     */


    /*
   查询skuInfo的基本信息
    */
    @GetMapping("/skudetail/info/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable("skuId") Long skuId);


    /*
    查询sku图片信息
     */

    @GetMapping("/skudetail/images/{skuId}")
    public Result<List<SkuImage>> getSkuImage(@PathVariable("skuId") Long skuId) ;

     /*
    查询实时价格
     */

    @GetMapping("/skudetail/price/{skuId}")
    public Result<BigDecimal> getSku1010Price(@PathVariable("skuId") Long skuId) ;



    /*
         查销售属性名和值,并且标记出当前sku是哪个
     */

    @GetMapping("/skudetail/saleattrvalues/{skuId}/{spuId}")
    public Result<List<SpuSaleAttr>> getSkuSaleattrvalues(@PathVariable("skuId") Long skuId,
                                                          @PathVariable("spuId") Long spuId) ;

    /*
        查询sku组合
     */

    @GetMapping("/skudetail/valuejson/{spuId}")
    public Result<String> getSkuValueJson(@PathVariable("spuId") Long spuId) ;

    /*
            查询分类
     */

    @GetMapping("/skudetail/categoryview/{c3Id}")
    public Result<CategoryViewTo> getSkuCategoryview(@PathVariable("c3Id") Long c3Id) ;




}
