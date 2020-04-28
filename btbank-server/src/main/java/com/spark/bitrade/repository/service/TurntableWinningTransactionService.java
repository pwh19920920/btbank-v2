package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.TurntableWinningTransaction;

import java.math.BigDecimal;

/**
 * 自动发放记录表(TurntableWinningTransaction)表服务接口
 *
 * @author biu
 * @since 2020-01-09 10:05:01
 */
public interface TurntableWinningTransactionService extends IService<TurntableWinningTransaction> {

    boolean saveRecord(Long id, Long memberId, Long rewardId, BigDecimal amount, String outcomeTxId);

    boolean confirm(TurntableWinningTransaction tx, String incomeTxid);
}