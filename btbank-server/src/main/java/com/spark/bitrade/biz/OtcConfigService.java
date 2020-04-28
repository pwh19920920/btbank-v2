package com.spark.bitrade.biz;

import java.util.function.Function;

/**
 * @author ww
 * @time 2019.11.28 10:26
 */
public interface OtcConfigService {
    /**
     * 获取配置的值
     *
     * @return
     */

    String getValue(String key);

    /**
     * 获取暂停商业营业的广告一直下架时间
     *
     * @return
     */
    Integer getOtcPausedBusinessAdOffHourSpan();

    /**
     * 获取当长时间休业后暂停商业时间
     *
     * @return
     */
    int getOtcPausedBusinessHoursWhenClosingLongTime();

    default <T> T getValue(String key, Function<String, T> convert, T defaultValue) {
        String value = getValue(key);
        if (value == null) {
            return defaultValue;
        }
        return convert.apply(value);
    }
}
