package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author SPF
* @description 针对表【spu_info(商品表)】的数据库操作Service
* @createDate 2022-08-23 21:26:25
*/
public interface SpuInfoService extends IService<SpuInfo> {

    void saveInfo(SpuInfo info);
}
