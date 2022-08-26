package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.jdbc.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    spu功能
 */
@RestController
@RequestMapping("/admin/product")
public class SpuController {

    /*
     * 分页获取Spu
     * @PathVariable： 路径变量
     * @RequestParam：请求参数（请求体中的某个数据）
     * @RequestBody： 请求参数（请求体的所有数据拿来）
     * @RequestHeader： 请求头
     * 什么是？  无论是？以后的数据还是请求体的数据，都叫请求参数
     * http://192.168.200.100:9000/admin/product/1/10?category3Id=2
     * ？以前的是请求路径， @PathVariable 在这里工作
     * ？以后请求参数： @RequestParam
     * 如果是Post请求。请求参数既可以放到url？以后，也可以放请求体；
     *      - @RequestParam： ？以后和请求体都能取
     * 如果是Get请求。请求参数需要放到url后面。
     *      - @RequestParam： ？以后和请求体都能取
     *
     * 发一个请求：
     * 请求首行：  \n  GET  http://xxxxx?djajda=dajkl
     * 请求头：    \n  Content-Type: xxx , xxx
     * 请求体：   \n 任意数据
     * 负载：请求参数（？后面的和请求体）【@RequestParam工作的地方】
     */

    @Autowired
    SpuInfoService spuInfoService;

    @Autowired
    SpuImageService spuImageService;

    // 1/10?category3Id=2
    @GetMapping("/{num}/{size}")
    public Result getSpuPage(@PathVariable("num") Long num,
                             @PathVariable("size") Long size,
                             @RequestParam("category3Id") Long category3Id){

        Page<SpuInfo> infoPage = new Page<>(num,size);
        QueryWrapper<SpuInfo> wrapper  = new QueryWrapper<>();
        wrapper.eq("category3_id",category3Id);
        Page<SpuInfo> page = spuInfoService.page(infoPage, wrapper);
        return Result.ok(page);
    }

    // admin/product/saveSpuInfo
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo info){
        spuInfoService.saveInfo(info);
        return Result.ok();
    }


    @GetMapping("/spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId") Long spuId){

        QueryWrapper<SpuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id",spuId);
        List<SpuImage> list = spuImageService.list(wrapper);
        return Result.ok(list);
    }



}
