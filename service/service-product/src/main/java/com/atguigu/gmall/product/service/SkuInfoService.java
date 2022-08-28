package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
* @author SPF
* @description 针对表【sku_info(库存单元表)】的数据库操作Service
* @createDate 2022-08-23 21:26:25
*/
public interface SkuInfoService extends IService<SkuInfo> {

    void saveSkuInfo(SkuInfo skuInfo);

    void cancelSale(Long skuId);

    void onSale(Long skuId);

    SkuDetailTo getSkuDetail(Long skuId);

    /*
    获取sku的实时价格
     */
    BigDecimal get1010Price(Long skuId);

    SkuInfo getDetailSkuInfo(Long skuId);

    List<SkuImage> getDetailSkuImages(Long skuId);
}
