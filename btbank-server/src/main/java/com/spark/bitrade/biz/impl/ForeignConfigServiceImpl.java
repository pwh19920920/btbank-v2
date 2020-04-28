package com.spark.bitrade.biz.impl;

import com.spark.bitrade.biz.ForeignConfigService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.constant.OtcConfigType;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.ForeignConfigDataDict;
import com.spark.bitrade.repository.entity.OtcConfigDataDict;
import com.spark.bitrade.repository.service.ForeignConfigDataDictService;
import com.spark.bitrade.repository.service.OtcConfigDataDictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@Slf4j
public class ForeignConfigServiceImpl implements ForeignConfigService {
    @Autowired
     ForeignConfigDataDictService foreignConfigDataDictService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String getValue(String key) {
        String value = "";
        String redisKey = BtBankSystemConfig.REDIS_FOREIGN_DICT_PREFIX + key;

        Object o = redisTemplate.opsForValue().get(redisKey);
        if (o != null) {
            value = o.toString();
        } else {
            ForeignConfigDataDict configDataDict = foreignConfigDataDictService.getValue(key);
            if (configDataDict != null) {
                value = configDataDict.getDictVal();
                redisTemplate.opsForValue().set(redisKey, value);
            }
        }


        if (value == null || StringUtils.isBlank(value)) {
            log.error("can not find key[{}] value", key);
            throw new BtBankException(BtBankMsgCode.FAILED_TO_LOAD_SYSTEM_CONFIGURATION.getCode(), BtBankMsgCode.FAILED_TO_LOAD_SYSTEM_CONFIGURATION.getMessage());
        }
        return value;
    }
    @Override
    public <T> T getConfig(String key, Function<Object, T> convert, T defaultValue) {
        Object value = getValue(key);
        if (value == null) {
            return defaultValue;
        }
        return convert.apply(value);
    }
}
