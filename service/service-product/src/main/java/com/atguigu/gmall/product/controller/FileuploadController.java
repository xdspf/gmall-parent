package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/product")
public class FileuploadController {

/*
 * 文件上传功能
 * 1、前端把文件流放到哪里了？我们该怎么拿到？
 *     Post请求数据在请求体（包含了文件[流]）
 * 如何接：
 * @RequestParam("file")MultipartFile file
 * @RequestPart("file")MultipartFile file: 专门处理文件的
 *
 * 各种注解接不通位置的请求数据
 * @RequestParam: 无论是什么请求 接请求参数； 用一个Pojo把所有数据都接了
 * @RequestPart： 接请求参数里面的文件项
 * @RequestBody： 接请求体中的所有数据 (json转为pojo)
 * @PathVariable: 接路径上的动态变量
 * @RequestHeader: 获取浏览器发送的请求的请求头中的某些值
 * @CookieValue： 获取浏览器发送的请求的Cookie值
 * - 如果多个就写数据，否则就写单个对象
 */


    //admin/product
    @PostMapping("/fileUpload")
    public Result fileUpload(@RequestPart("file")MultipartFile file){

        //TODO

        return Result.ok();
    }

    @PostMapping("/reg")
    public Result regUpload(@RequestParam("username") String username,
                            @RequestParam("password") String password,
                            @RequestParam("email") String email,
                            @RequestPart("header") MultipartFile[] header,
                            @RequestPart("header") MultipartFile photo,
                            @RequestPart("header") MultipartFile card,
                            @RequestParam("ah") String[] ah,
                            @RequestHeader("Cache-Control") String cache,
                            @CookieValue("jessionid") String jessionid
                            ){

        Map<String,Object> result = new HashMap<>();
        result.put("用户名:",username);
        result.put("密码:",password);
        result.put("邮箱:",email);
        result.put("爱好:", Arrays.asList(ah));
        result.put("cache", cache);


        result.put("头像文件大小:",header.length);
        result.put("生活照文件大小:",photo.getSize());
        result.put("身份证文件大小:",card.getSize());

        return Result.ok(result);
    }




}
