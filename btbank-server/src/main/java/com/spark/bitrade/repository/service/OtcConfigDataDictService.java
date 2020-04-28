package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.OtcConfigDataDict;

/**
 * btbank规则配置(OtcConfigDataDict)表服务接口
 *
 * @author daring5920
 * @since 2019-11-27 17:53:30
 */
public interface OtcConfigDataDictService extends IService<OtcConfigDataDict> {

    OtcConfigDataDict getValue(String key);
}