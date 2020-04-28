package com.spark.bitrade.biz;

import java.util.function.Function;

/**
 * @author mahao
 * @time 2020-02-04 10:26
 */
public interface ForeignConfigService {
    /**
     * 获取配置的值
     *
     * @return
     */

    String getValue(String key);
    <T> T getConfig(String key, Function<Object, T> convert, T defaultValue);

}
