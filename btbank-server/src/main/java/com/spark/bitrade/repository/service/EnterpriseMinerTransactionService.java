package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.EnterpriseMinerTransaction;

import java.math.BigDecimal;

/**
 * 企业矿工流水表(EnterpriseMinerTransaction)表服务接口
 *
 * @author biu
 * @since 2019-12-23 17:14:35
 */
public interface EnterpriseMinerTransactionService extends IService<EnterpriseMinerTransaction> {

    BigDecimal sumRewardOfYesterday(Long memberId);

    EnterpriseMinerTransaction preTransfer(Long memberId, BigDecimal amount);

    boolean confirmTransfer(EnterpriseMinerTransaction tx, Long recordId);

    EnterpriseMinerTransaction mining(Integer minerId, BigDecimal amount, String orderSn);

    EnterpriseMinerTransaction preReward(EnterpriseMinerTransaction tx);

    boolean isNotProcessed(EnterpriseMinerTransaction tx);

    boolean collect(EnterpriseMinerTransaction tx, Long recordId);

    boolean confirmReward(EnterpriseMinerTransaction tx, Long recordId);

    boolean isExistOrder(String orderSn);
}