package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SPF
 * @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
 * @createDate 2022-08-23 21:26:25
 */
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
        implements BaseAttrInfoService {

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    /**
     * @param c1Id 一级分类id
     * @param c2Id 二级分类id
     * @param c3Id 三级分类id
     * @return
     */

    @Override
    public List<BaseAttrInfo> getAttrInfoValueByCategoryId(Long c1Id, Long c2Id, Long c3Id) {

        //查询指定分类下的所有属性值和属性值
        List<BaseAttrInfo> infos = baseAttrInfoMapper.getAttrInfoValueByCategoryId(c1Id, c2Id, c3Id);
        return infos;

    }

    /**
     * 保存平台属性
     *
     * @param info
     */
    @Override
    public void saveAttrInfo(BaseAttrInfo info) {

        if (info.getId() == null) {
            //1.新增属性
            insertBaseAttrInfo(info);
        } else {
            //2.修改属性
            updateBaseAttrInfo(info);
        }
    }

    private void updateBaseAttrInfo(BaseAttrInfo info) {
        //2.1修改属性名信息
        baseAttrInfoMapper.updateById(info);
        //2.2修改属性值
        List<BaseAttrValue> valueList = info.getAttrValueList();

        //删除数据
        //1.获取所有的id
        List<Long> vid = new ArrayList<>();
        for (BaseAttrValue attrValue : valueList) {
            Long id = attrValue.getId();
            if (id != null){
                vid.add(id);
            }
        }

        //判断集合中有没有id，如果没有id，不需要删除  非空判断
        if (vid.size() > 0){ //集合不为空
            //单个删除
            QueryWrapper<BaseAttrValue> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("attr_id", info.getId());
            deleteWrapper.notIn("id",vid);
            baseAttrValueMapper.delete(deleteWrapper);

        }else {
            //全部删除
            QueryWrapper<BaseAttrValue> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("attr_id", info.getId());
            baseAttrValueMapper.delete(deleteWrapper);

        }

        for (BaseAttrValue attrValue : valueList) {

            //修改数据
            if (attrValue.getId() != null) {
                baseAttrValueMapper.updateById(attrValue);
            }

            if (attrValue.getId() == null) {
                //新增数据
                attrValue.setAttrId(info.getId());
                baseAttrValueMapper.insert(attrValue);
            }
        }
    }

    private void insertBaseAttrInfo(BaseAttrInfo info) {
        //1.保存属性名
        baseAttrInfoMapper.insert(info);
        Long id = info.getId(); //获取到保存的属性名的自增id

        //2.保存属性值
        List<BaseAttrValue> attrValueList = info.getAttrValueList();
        for (BaseAttrValue baseAttrValue : attrValueList) {
            baseAttrValue.setAttrId(id);  //回填属性名记录的自增id
            baseAttrValueMapper.insert(baseAttrValue);
        }
    }
}




