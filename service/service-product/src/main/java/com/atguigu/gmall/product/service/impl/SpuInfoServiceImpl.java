package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SpuImageMapper;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author SPF
* @description 针对表【spu_info(商品表)】的数据库操作Service实现
* @createDate 2022-08-23 21:26:25
*/
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
    implements SpuInfoService{

    @Autowired
    SpuInfoMapper spuInfoMapper;

    @Autowired
    SpuImageService spuImageService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @Autowired
    SpuSaleAttrValueService spuSaleAttrValueService;

    @Transactional //事务
    @Override
    public void saveInfo(SpuInfo info) {
        //1.把spu基本信息保存到spu_info中
        spuInfoMapper.insert(info);
        Long id = info.getId();

        //2.把spu的图片保存到spu_image
        List<SpuImage> spuImageList = info.getSpuImageList();
        for (SpuImage spuImage : spuImageList) {
            //回填spu_id
            spuImage.setSpuId(id);
        }
        spuImageService.saveBatch(spuImageList);//因为mapper不能进行批量保存，因此用service

        //3.保存销售属性名
        List<SpuSaleAttr> spuSaleAttrList = info.getSpuSaleAttrList();
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            spuSaleAttr.setSpuId(id);
            //4.保存销售属性值
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                spuSaleAttrValue.setSpuId(id);
                String saleAttrName = spuSaleAttr.getSaleAttrName();
                spuSaleAttrValue.setSaleAttrName(saleAttrName);
            }
            spuSaleAttrValueService.saveBatch(spuSaleAttrValueList);
        }
        //保存数据到数据库
        spuSaleAttrService.saveBatch(spuSaleAttrList);

    }
}




