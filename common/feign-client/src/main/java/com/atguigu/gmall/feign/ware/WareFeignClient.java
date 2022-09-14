package com.atguigu.gmall.feign.ware;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "ware-manage",url = "${app.ware-url:http://localhost:9007/}")
//@FeignClient(value = "ware-manage",url = "https://search.bilibili.com" )
public interface WareFeignClient {
    //    //all?keyword=哈哈
//    @GetMapping(value = "/all",produces = "text/html;charset=utf-8")
//    String search(@RequestParam("keyword") String keyword);


    /**
     * 查询一个商品是否有库存
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/hasStock")
    String hasStock(@RequestParam("skuId") Long skuId,
                    @RequestParam("num") Integer num);


}
