package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.ForeignConfigDataDict;

/**
 * btbank外汇规则配置(ForeignConfigDataDict)表服务接口
 *
 * @author yangch
 * @since 2020-02-04 11:43:23
 */
public interface ForeignConfigDataDictService extends IService<ForeignConfigDataDict> {
    ForeignConfigDataDict getValue(String key);
}