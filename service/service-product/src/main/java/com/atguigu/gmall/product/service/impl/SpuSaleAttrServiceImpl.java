package com.atguigu.gmall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author SPF
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service实现
* @createDate 2022-08-23 21:26:25
*/
@Service
public class SpuSaleAttrServiceImpl extends ServiceImpl<SpuSaleAttrMapper, SpuSaleAttr>
    implements SpuSaleAttrService{

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Override
    public List<SpuSaleAttr> getSaleAttrAndValueBySpuId(Long id) {
        List<SpuSaleAttr> spuSaleAttrs =   spuSaleAttrMapper.getSaleAttrAndValueBySpuId(id);

        return spuSaleAttrs;
    }

    @Override
    public List<SpuSaleAttr> getSaleAttrAndValueMarkSpu(Long spuId, Long skuId) {

        return spuSaleAttrMapper.getSaleAttrAndValueMarkSpu(spuId,skuId);
    }
}




