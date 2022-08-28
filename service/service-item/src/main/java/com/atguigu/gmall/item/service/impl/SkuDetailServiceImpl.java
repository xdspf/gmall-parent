package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.feign.SkuDetailFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;

    @Autowired
    ThreadPoolExecutor executor;

//    @Override
//    public SkuDetailTo getSkuDetail(Long skuId) {
//        Result<SkuDetailTo> skuDetail = skuDetailFeignClient.getSkuDetail(skuId);
//
//        return skuDetail.getData();
//    }


    /*
            优化
     */
    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        SkuDetailTo detailTo = new SkuDetailTo();


        Result<SkuInfo> result = skuDetailFeignClient.getSkuInfo(skuId);
        //1.查基本信息
        SkuInfo skuInfo = result.getData();
        detailTo.setSkuInfo(skuInfo);

        //2.查商品图片信息
        Result<List<SkuImage>> skuImage = skuDetailFeignClient.getSkuImage(skuId);
        skuInfo.setSkuImageList(skuImage.getData());


        //3.查商品实时价格
        Result<BigDecimal> sku1010Price = skuDetailFeignClient.getSku1010Price(skuId);
        detailTo.setPrice(sku1010Price.getData());

        //4.查销售属性名和值
        Result<List<SpuSaleAttr>> saleattrvalues = skuDetailFeignClient.getSkuSaleattrvalues(skuId, skuInfo.getSpuId());
        detailTo.setSpuSaleAttrList(saleattrvalues.getData());

        //5.sku组合
        Result<String> skuValueJson = skuDetailFeignClient.getSkuValueJson(skuInfo.getSpuId());
        detailTo.setValuesSkuJson(skuValueJson.getData());

        //6.查分类
        Result<CategoryViewTo> categoryview = skuDetailFeignClient.getSkuCategoryview(skuInfo.getCategory3Id());
        detailTo.setCategoryView(categoryview.getData());

        return detailTo;
    }
}
