package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.BaseCategory3;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author SPF
* @description 针对表【base_category3(三级分类表)】的数据库操作Service
* @createDate 2022-08-23 08:30:27
*/
public interface BaseCategory3Service extends IService<BaseCategory3> {

    List<BaseCategory3> getCategoryChild(Long c2Id);
}
