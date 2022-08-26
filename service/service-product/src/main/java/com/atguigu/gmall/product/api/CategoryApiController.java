package com.atguigu.gmall.product.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
    分类有关的api
     /api/inner/rpc/模块名/路径  远程调用都是内部接口
 */
@Api(tags = "三级分类的RPC接口")
@RestController  //返回json数据
@RequestMapping("/api/inner/rpc/product")
public class CategoryApiController {

    @Autowired
    BaseCategory2Service baseCategory2Service;

    /*
        查询所有分类并封装成树形菜单结构
     */

    @GetMapping("/category/tree")
    @ApiOperation("三级分类树形结构查询")
    public Result getAllCategoryWithTree() {

        List<CategoryTreeTo> categoryTreeToList = baseCategory2Service.getAllCategoryWithTree();
        return Result.ok(categoryTreeToList);
    }
}
