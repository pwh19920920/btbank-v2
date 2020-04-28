package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.TurntableWinningTransaction;
import com.spark.bitrade.repository.mapper.TurntableWinningTransactionMapper;
import com.spark.bitrade.repository.service.TurntableWinningTransactionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 自动发放记录表(TurntableWinningTransaction)表服务实现类
 *
 * @author biu
 * @since 2020-01-09 10:05:01
 */
@Service("turntableWinningTransactionService")
public class TurntableWinningTransactionServiceImpl extends ServiceImpl<TurntableWinningTransactionMapper, TurntableWinningTransaction> implements TurntableWinningTransactionService {

    @Override
    public boolean saveRecord(Long id, Long memberId, Long rewardId, BigDecimal amount, String outcomeTxId) {
        TurntableWinningTransaction tx = new TurntableWinningTransaction();

        tx.setId(id);
        tx.setMemberId(memberId);
        tx.setRewardMemberId(rewardId);
        tx.setCoinUnit("BT");
        tx.setAmount(amount);
        tx.setOutcomeTxid(outcomeTxId);
        tx.setState(0);
        tx.setCreateTime(new Date());

        return save(tx);
    }

    @Override
    public boolean confirm(TurntableWinningTransaction tx, String incomeTxid) {
        UpdateWrapper<TurntableWinningTransaction> wrapper = new UpdateWrapper<>();

        wrapper.eq("id", tx.getId()).eq("state", 1)
                .set("income_txid", incomeTxid).set("state", 2).set("update_time", new Date());

        return update(wrapper);
    }
}