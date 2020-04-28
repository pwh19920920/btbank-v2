package com.spark.bitrade.biz.impl;

import com.spark.bitrade.biz.OtcConfigService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.constant.OtcConfigType;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.OtcConfigDataDict;
import com.spark.bitrade.repository.service.OtcConfigDataDictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author ww
 * @time 2019.11.28 10:27
 */
@Service
@Slf4j
public class OtcConfigServiceImpl implements OtcConfigService {

    @Autowired
    OtcConfigDataDictService configDataDictService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String getValue(String key) {
        String value = "";
        String redisKey = BtBankSystemConfig.REDIS_OTC_DICT_PREFIX + key;

        Object o = redisTemplate.opsForValue().get(redisKey);
        if (o != null) {
            value = o.toString();
        } else {
            OtcConfigDataDict configDataDict = configDataDictService.getValue(key);
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
    public Integer getOtcPausedBusinessAdOffHourSpan() {

        String value = getValue(OtcConfigType.OTC_PAUSED_BIZ_AD_OFF_HOUR_SPAN);

        if (value != null && value != "") {
            return Integer.valueOf(value);
        }

        return 0;
    }

    @Override
    public int getOtcPausedBusinessHoursWhenClosingLongTime() {

        String value = getValue(OtcConfigType.OTC_PAUSED_BIZ_HOURS_WHEN_CLOSING_LONG_TIME);
        if (value != null && value != "") {
            return Integer.valueOf(value);
        }

        return 0;
    }
}
