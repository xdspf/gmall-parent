package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.web.feign.CategoryFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller  //页面跳转@Controller   给浏览器返回json用@RestController
public class IndexController {

    @Autowired
    CategoryFeignClient categoryFeignClient;

    /*
        跳转首页
     */
    @GetMapping({"/","/index"}) //访问首页
    public  String indexPage(Model model){
        Result<List<CategoryTreeTo>> result = categoryFeignClient.getAllCategoryWithTree();
        if (result.isOk()){ //远程调用成功
            List<CategoryTreeTo> data = result.getData();
            model.addAttribute("list",data);
        }
        return "index/index"; //页面的逻辑视图名 (classpath:/templates/index/index.html)
    }
}
