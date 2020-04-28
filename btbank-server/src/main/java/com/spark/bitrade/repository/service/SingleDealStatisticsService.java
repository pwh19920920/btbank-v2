package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.SingleDealStatistics;


/**
 * 单个交易日OTC统计(RankRewardTransaction)表数据库访问层
 *
 * @author qiuyuanjie
 * @since 2020-03-25 15:14:10
 */
public interface SingleDealStatisticsService extends IService<SingleDealStatistics> {

    /**
     * 统计单个交易日的OTC购买出售情况
     * @return
     */
    SingleDealStatistics staSingleTransactionOTC(String startTime,String endTime);
}
