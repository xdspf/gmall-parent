package com.atguigu.gmall.item.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.to.SkuDetailTo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "三级分类的RPC接口")
@RestController  //返回json数据
@RequestMapping("/api/inner/rpc/item")
public class SkuDetaiApiController {

    @Autowired
    SkuDetailService skuDetailService;


    @RequestMapping("/skudetail/{skuId}")
    public Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId") Long skuId){

        //商品的详情
       SkuDetailTo skuDetailTo =  skuDetailService.getSkuDetail(skuId);

       //更新热度分  攒一批更新一下。 涨到100分增加一次
        skuDetailService.updateHotScore(skuId);
        return Result.ok(skuDetailTo);
    }

}
