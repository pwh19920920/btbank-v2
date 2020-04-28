package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.OtcConfigDataDict;
import com.spark.bitrade.repository.mapper.ForeignConfigDataDictMapper;
import com.spark.bitrade.repository.entity.ForeignConfigDataDict;
import com.spark.bitrade.repository.service.ForeignConfigDataDictService;
import org.springframework.stereotype.Service;

/**
 * btbank外汇规则配置(ForeignConfigDataDict)表服务实现类
 *
 * @author yangch
 * @since 2020-02-04 11:43:23
 */
@Service("foreignConfigDataDictService")
public class ForeignConfigDataDictServiceImpl extends ServiceImpl<ForeignConfigDataDictMapper, ForeignConfigDataDict> implements ForeignConfigDataDictService {

    @Override
    public ForeignConfigDataDict getValue(String key) {
        ForeignConfigDataDict foreignConfigDataDict =  baseMapper.selectOne(new LambdaQueryWrapper<ForeignConfigDataDict>()
                .eq(ForeignConfigDataDict::getDictKey, key).eq(ForeignConfigDataDict::getStatus, 1).last("limit 1"));
        return foreignConfigDataDict;
    }
}