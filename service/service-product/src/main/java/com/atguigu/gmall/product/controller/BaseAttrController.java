package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 平台属性
 */
@RestController
@RequestMapping("/admin/product")
public class BaseAttrController {

    @Autowired
    BaseAttrInfoService baseAttrInfoService;

    @Autowired
    BaseAttrValueService baseAttrValueService;

    /*
    查询某个分类下的所有平台属性
     */
    @GetMapping("/attrInfoList/{c1Id}/{c2Id}/{c3Id}")
    public Result getAttrInfoList(@PathVariable("c1Id") Long c1Id,
                                  @PathVariable("c2Id") Long c2Id,
                                  @PathVariable("c3Id") Long c3Id) {

        List<BaseAttrInfo> infos = baseAttrInfoService.getAttrInfoValueByCategoryId(c1Id, c2Id, c3Id);
        return Result.ok(infos);
    }

    /*
    保存属性信息，前端把所有页面录入的数据以json的方式post传给我们
    请求体：{"id":null,"attrName":null,"category1Id":0,"category2Id":0,"category3Id":0,"attrValueList":[],"categoryId":1,"categoryLevel":1}
     */

    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo info) {

        baseAttrInfoService.saveAttrInfo(info);
        return Result.ok();
    }

    //http://192.168.200.1/admin/product/getAttrValueList/11
    /*
     *根据平台属性id,获取这个属性的完整信息（属性名、属性值）
     */
    @GetMapping("/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId") Long attrId) {

        List<BaseAttrValue> attrValueList = baseAttrValueService.getAttrValueList(attrId);
        return Result.ok(attrValueList);
    }

}
