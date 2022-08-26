package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product")
public class SkuController {

    @Autowired
    SkuInfoService skuInfoService;

    @GetMapping("/list/{num}/{size}")
    public Result getPage(@PathVariable("num") Long num,@PathVariable("size") Long size){

        Page<SkuInfo> skuInfoPage = new Page<>(num,size);
        Page<SkuInfo> page = skuInfoService.page(skuInfoPage);
        return Result.ok(page);
    }


    /*
    接前端的json数据，可以使用逆向方式生成vo【和前端对接的JavaBean】
     * https://www.json.cn/json/json2java.html  根据json模型生成vo
     */
    //admin/product/saveSkuInfo
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        //保存sku
        skuInfoService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    /*
    下架
     */

    //admin/product/cancelSale/46
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId){
        skuInfoService.cancelSale(skuId);
        return Result.ok();
    }

    /*
    上架
     */

    //  admin/product/onSale/49
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId){
        skuInfoService.onSale(skuId);
        return Result.ok();
    }


}

