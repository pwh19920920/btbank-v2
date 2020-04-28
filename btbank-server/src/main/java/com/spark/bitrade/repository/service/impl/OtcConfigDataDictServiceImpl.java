package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.OtcConfigDataDict;
import com.spark.bitrade.repository.mapper.OtcConfigDataDictMapper;
import com.spark.bitrade.repository.service.OtcConfigDataDictService;
import org.springframework.stereotype.Service;

/**
 * btbank规则配置(OtcConfigDataDict)表服务实现类
 *
 * @author daring5920
 * @since 2019-11-27 17:53:30
 */
@Service("otcConfigDataDictService")
public class OtcConfigDataDictServiceImpl extends ServiceImpl<OtcConfigDataDictMapper, OtcConfigDataDict> implements OtcConfigDataDictService {

    @Override
    public OtcConfigDataDict getValue(String key) {
        OtcConfigDataDict dataDict = baseMapper.selectOne(new LambdaQueryWrapper<OtcConfigDataDict>()
                .eq(OtcConfigDataDict::getDictKey, key).eq(OtcConfigDataDict::getStatus, 1).last("limit 1"));
        return dataDict;
    }
}