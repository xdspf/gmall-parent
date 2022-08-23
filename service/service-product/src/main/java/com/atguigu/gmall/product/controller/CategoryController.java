package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.service.BaseCategory1Service;

import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类请求处理器
 * 前后分离：前端发请求，后台处理好后响应JSON数据
 * 所有请求全部返回 Result 对象的JSON。所有要携带的数据放到Result的data属性内即可
 */


//@ResponseBody   所有的响应数据都直接写给浏览器（如果是对象写成json，如果是文本就写成普通字符串）
//@Controller 这个类是来接受请求的
@RestController
@RequestMapping("/admin/product")
public class CategoryController {

    @Autowired
    BaseCategory1Service baseCategory1Service;

    @Autowired
    BaseCategory2Service baseCategory2Service;

    @Autowired
    BaseCategory3Service baseCategory3Service;



    /*
    查询所有的一级分类
     */

    @GetMapping("/getCategory1")
    public Result getCategory1() {

        List<BaseCategory1> list = baseCategory1Service.list();

        return Result.ok(list);
    }

    /*
    获取某个一级分类下的所有二级分类
     */

    //  admin/product/getCategory2/id
    @GetMapping("/getCategory2/{c1Id}")
    public Result getCategory2(@PathVariable("c1Id") Long c1Id) {

        List<BaseCategory2> category2 = baseCategory2Service.getCategory1Child(c1Id);

        return Result.ok(category2);
    }

    /**
     * 获取二级分类下的三级分类
     * @param c2Id
     * @return
     */
    @GetMapping("/getCategory3/{c2Id}")
    public Result getCategory3(@PathVariable("c2Id") Long c2Id) {

        List<BaseCategory3> category3 = baseCategory3Service.getCategory2Child(c2Id);

        return Result.ok(category3);
    }


}
