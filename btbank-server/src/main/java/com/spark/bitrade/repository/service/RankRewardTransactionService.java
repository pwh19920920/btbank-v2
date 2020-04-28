package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.RankRewardTransaction;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * 奖励金额流水(RankRewardTransaction)表服务接口
 *
 * @author daring5920
 * @since 2019-12-17 15:14:10
 */
public interface RankRewardTransactionService extends IService<RankRewardTransaction> {

    /**
     * 发放两种每日收益奖励
     * @return
     */
    void getAndInsertReward();

    /**
     * 写入累计收益奖励数据
     */
    void insertTotalReward();

    List<RankRewardTransaction> getRankListByType(RankRewardTransaction rankRewardTransaction);
}