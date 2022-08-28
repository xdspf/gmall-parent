package com.atguigu.gmall.product.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/*
商品详情数据库层操作
 */
@RestController
@RequestMapping("/api/inner/rpc/product")
public class SkuDetailApiController {

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @Autowired
    BaseCategory3Service baseCategory3Service;
    /*
        数据库层真正查询商品详情
     */

//    @GetMapping("/skudetail/{skuId}")
//    public Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId") Long skuId) {
//       SkuDetailTo skuDetailTo =  skuInfoService.getSkuDetail(skuId);
//        return Result.ok(skuDetailTo);
//    }

    /*
            优化
     */

    /*
    查询skuInfo的基本信息
     */
    @GetMapping("/skudetail/info/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable("skuId") Long skuId) {
        SkuInfo skuInfo = skuInfoService.getDetailSkuInfo(skuId);
        return Result.ok(skuInfo);
    }


    /*
    查询sku图片信息
     */

    @GetMapping("/skudetail/images/{skuId}")
    public Result<List<SkuImage>> getSkuImage(@PathVariable("skuId") Long skuId) {
        List<SkuImage> images = skuInfoService.getDetailSkuImages(skuId);
        return Result.ok(images);
    }

     /*
    查询实时价格
     */

    @GetMapping("/skudetail/price/{skuId}")
    public Result<BigDecimal> getSku1010Price(@PathVariable("skuId") Long skuId) {
        BigDecimal price = skuInfoService.get1010Price(skuId);
        return Result.ok(price);
    }



    /*
         查销售属性名和值,并且标记出当前sku是哪个
     */

    @GetMapping("/skudetail/saleattrvalues/{skuId}/{spuId}")
    public Result<List<SpuSaleAttr>> getSkuSaleattrvalues(@PathVariable("skuId") Long skuId,
                                                   @PathVariable("spuId") Long spuId) {
        List<SpuSaleAttr> saleAttrList = spuSaleAttrService.getSaleAttrAndValueMarkSku(spuId, skuId);
        return Result.ok(saleAttrList);
    }

    /*
        查询sku组合
     */

    @GetMapping("/skudetail/valuejson/{spuId}")
    public Result<String> getSkuValueJson(@PathVariable("spuId") Long spuId) {
        String valueJson = spuSaleAttrService.getAllSkuSaleAttrValueJson(spuId);
        return Result.ok(valueJson);
    }

    /*
            查询分类
     */

    @GetMapping("/skudetail/categoryview/{c3Id}")
    public Result<CategoryViewTo> getSkuCategoryview(@PathVariable("c3Id") Long c3Id) {
        CategoryViewTo categoryViewTo = baseCategory3Service.getCategoryView(c3Id);
        return Result.ok(categoryViewTo);
    }



}
