package com.spark.bitrade.biz;

import com.spark.bitrade.entity.Member;

import java.math.BigDecimal;

/**
 * 体验金
 * @author shenzucai
 * @time 2019.12.08 13:47
 */
public interface PlanAssetService {

    /**
     * type活动类型member用户解锁体验金amount数量
     * @author shenzucai
     * @time 2019.12.08 13:50
     * @param member
     * @param amount
     * @param type (1,参与挖矿，2推荐矿工，3挖矿收益)
     * @return true
     */
    Boolean doUnlock(Member member, BigDecimal amount,Integer type,Long minePoolId,Long miningId);
}
