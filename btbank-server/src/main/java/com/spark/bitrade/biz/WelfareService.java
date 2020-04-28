package com.spark.bitrade.biz;

import java.math.BigDecimal;

/**
 * WelfareService
 *
 * @author biu
 * @since 2020/4/9 16:38
 */
public interface WelfareService {

    /**
     * 检查前辈余额
     *
     * @param memberId mid
     * @param amount   amt
     * @return bool
     */
    boolean checkWalletBalance(Long memberId, BigDecimal amount);

    /**
     * 是否是有效矿工
     *
     * @param memberId mid
     * @return bool
     */
    boolean isAvailableMiner(Long memberId);

    /**
     * 创建当天的的活动
     *
     * @return bool
     */
    boolean autoCreateWelfarePacket();

    /**
     * 释放
     */
    void release();

    /**
     * 检测直推状态
     */
    void checkRewardStatus();
}
