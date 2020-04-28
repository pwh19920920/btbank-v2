package com.spark.bitrade.biz;

import com.spark.bitrade.repository.entity.FinancialActivityJoinDetails;

public interface MinerRebateService {

    /**
     * 处理矿工邀请奖励
     */
    void processRebate();

    void processFinancialSuperiorRewards(FinancialActivityJoinDetails fi, Long fatherId);
}
