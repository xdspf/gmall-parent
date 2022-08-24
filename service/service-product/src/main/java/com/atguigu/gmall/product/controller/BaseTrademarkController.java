package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.aspectj.apache.bcel.generic.RET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product")
public class BaseTrademarkController {

    @Autowired
    BaseTrademarkService baseTrademarkService;

    //admin/product/baseTrademark/{page}/{limit}
        //page：第几页  limit：每页数量
    @GetMapping("/baseTrademark/{page}/{limit}")
    public Result baseTrademark(@PathVariable("page") Long page,
                                @PathVariable("limit") Long limit){

        Page<BaseTrademark> page1 = new Page(page,limit);
        //分页查询
        Page<BaseTrademark> pageResult = baseTrademarkService.page(page1);
        return Result.ok(pageResult);
    }

        /*
        根据id查询品牌
        */
    @GetMapping("/baseTrademark/get/{id}")
    public Result getBaseTrademark(@PathVariable("id") Long id){
        BaseTrademark trademark = baseTrademarkService.getById(id);

        return Result.ok(trademark);
    }

    /*
    修改品牌
     */
    @PutMapping("/baseTrademark/update")
    public Result updateBaseTrademark(@RequestBody BaseTrademark baseTrademark){

        baseTrademarkService.updateById(baseTrademark);

        return Result.ok();
    }

    /*
    保存品牌
     */

    //admin/product/baseTrademark/save
    @PostMapping("baseTrademark/save")
    public Result saveBaseTrademark(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    /*
    删除品牌
     */

    //admin/product/baseTrademark/remove/13
    @DeleteMapping("/baseTrademark/remove/{id}")
    public Result removeBaseTrademark(@PathVariable("id") Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }



}
