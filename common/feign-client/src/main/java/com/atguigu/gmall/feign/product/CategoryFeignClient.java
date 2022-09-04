package com.atguigu.gmall.feign.product;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/inner/rpc/product")
@FeignClient("service-product")  //告诉springboot这是一个远程调用的客户端
public interface CategoryFeignClient {

    /*
     *1、 给 service-product 发送一个 GET方式的请求 路径是 /api/inner/rpc/product/category/tree
     *2、 拿到远程的响应 json 结果后转成 Result类型的对象，并且 返回的数据是 List<CategoryTreeTo>
     */

   /* @GetMapping("/api/inner/rpc/product/category/tree")
     Result<List<CategoryTreeTo>>  getCategoryTree();*/


    @GetMapping("/category/tree")
    public Result<List<CategoryTreeTo>>  getAllCategoryWithTree();

}
