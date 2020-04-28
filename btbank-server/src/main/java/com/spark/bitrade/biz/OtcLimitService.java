package com.spark.bitrade.biz;

import java.math.BigDecimal;

/**
 * OTC 限制查询服务接口
 *
 * @author biu
 * @since 2019/11/28 16:16
 */
public interface OtcLimitService {

    /**
     * 静止提现和转出的限制数量
     *
     * @param memberId 会员ID
     * @return amount
     */
    BigDecimal forbidToWithdrawAndTransferOut(Long memberId);

    /**
     * 余额是否充足
     *
     * @param memberId 会员ID
     * @param minimum  最低余额
     * @return bool
     */
    boolean balanceIsEnough(Long memberId, BigDecimal minimum);

    /**
     * 是否是内部商家
     *
     * @param memberId id
     * @return bool
     */
    boolean isInnerMerchant(Long memberId);

    /**
     * 是否在处罚中
     *
     * @param memberId id
     * @return bool
     */
    boolean isInPunishment(Long memberId);
}
