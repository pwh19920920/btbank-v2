package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.SingleDailyStatistics;

import java.util.List;

/**
 * (SingleDailyStatistics)表服务接口
 *
 * @author daring5920
 * @since 2020-03-23 13:42:21
 */
public interface SingleDailyStatisticsService extends IService<SingleDailyStatistics> {

    /**
     * 生成单个交易日内的OTC购买排名
     *
     * @return
     */
    List<SingleDailyStatistics> recordOTCBuyRank(String startTime, String endTime);

    /**
     * 生成单个交易日内的OTC出售排名
     *
     * @return
     */
    List<SingleDailyStatistics> recordOTCSellRank(String startTime, String endTime);

    /**
     * 单个交易日云端转入（内部转账）排名
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<SingleDailyStatistics> findPayFastReceiveRank(String startTime, String endTime);

    /**
     * 单个交易日云端转出（内部转账）排名
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<SingleDailyStatistics> findPayFastPayRank(String startTime, String endTime);

}