package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.BtBankDataDict;
import com.spark.bitrade.repository.entity.GuessingConfigDataDict;

/**
 * 竞猜活动规则配置(GuessingConfigDataDict)表服务接口
 *
 * @author daring5920
 * @since 2020-01-02 10:30:50
 */
public interface GuessingConfigDataDictService extends IService<GuessingConfigDataDict> {
    GuessingConfigDataDict findFirstByDictIdAndDictKey(String dictId, String dictKey);
}