package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.BtBankMemberScoreWallet;

import java.math.BigDecimal;

/**
 * <p>
 * 用户挖矿积分钱包表 服务类
 * </p>
 *
 * @author qhliao
 * @since 2020-03-18
 */
public interface BtBankMemberScoreWalletService extends IService<BtBankMemberScoreWallet> {

    BtBankMemberScoreWallet findOne(Long memberId);

    boolean decreaseScore(Long memberId, BigDecimal score);
}
