package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import org.aspectj.apache.bcel.generic.RET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
    销售属性
 */
@RestController
@RequestMapping("/admin/product")
public class BaseSaleAttrController {

    @Autowired
    BaseSaleAttrService baseSaleAttrService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    /*
    获取所有销售属性的名字（base_sale_attr表）
     */

    @GetMapping("/baseSaleAttrList")
    public Result getBaseSaleAttrList(){

        List<BaseSaleAttr> list = baseSaleAttrService.list();

        return Result.ok(list);
    }

    /*
    查询所有销售属性
     */

    //admin/product/spuSaleAttrList/29
    @GetMapping("/spuSaleAttrList/{id}")
    public Result spuSaleAttrList(@PathVariable("id") Long id){
      List<SpuSaleAttr>  spuSaleAttrs = spuSaleAttrService.getSaleAttrAndValueBySpuId(id);

        return Result.ok(spuSaleAttrs);
    }



}
