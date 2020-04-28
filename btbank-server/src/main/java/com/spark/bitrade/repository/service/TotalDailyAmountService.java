package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.TotalDailyAmount;
/**
 * total_daily_amount 表服务接口
 *
 * @author qiuyuanjie
 * @since 2020-03-09 16:48:01
 */
public interface TotalDailyAmountService extends IService<TotalDailyAmount> {

    /**
     * 每日定点统计用户资产
     */
    Boolean statTotalDailyAmount();

}
