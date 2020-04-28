package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.mapper.ImGroupMapper;
import com.spark.bitrade.repository.entity.ImGroup;
import com.spark.bitrade.repository.service.ImGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (ImGroup)表服务实现类
 *
 * @author yangch
 * @since 2020-01-20 10:58:22
 */
@Service("imGroupService")
public class ImGroupServiceImpl extends ServiceImpl<ImGroupMapper, ImGroup> implements ImGroupService {


    @Override
    public List<ImGroup> getAvailableGroup() {
        QueryWrapper<ImGroup> wrapper = new QueryWrapper<>();
        wrapper.lambda().le(ImGroup::getGroupSize,500).orderByDesc(ImGroup::getGroupSize);
        return this.list(wrapper);
    }
}