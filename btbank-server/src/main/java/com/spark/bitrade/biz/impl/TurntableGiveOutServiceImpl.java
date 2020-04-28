package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.spark.bitrade.biz.TurntableGiveOutService;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.TurntableWinning;
import com.spark.bitrade.repository.entity.TurntableWinningTransaction;
import com.spark.bitrade.repository.service.TurntableWinningService;
import com.spark.bitrade.repository.service.TurntableWinningTransactionService;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.SpringContextUtil;
import com.spark.bitrade.util.StatusUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * TurntableGiveOutServiceImpl
 *
 * @author biu
 * @since 2020/1/9 9:26
 */
@Slf4j
@Service
@AllArgsConstructor
public class TurntableGiveOutServiceImpl implements TurntableGiveOutService {

    private BtBankConfigService configService;
    private MemberWalletService memberWalletService;
    private TurntableWinningService winningService;
    private TurntableWinningTransactionService winningTransactionService;

    @Async
    @Override
    public void async(TurntableWinning winning) {
        try {
            getService().handle(winning);
        } catch (RuntimeException ex) {
            log.error("async handel error -> {}", winning);
            log.error("async handel error", ex);
        }
    }

    @Async
    @Override
    public void async(TurntableWinningTransaction tx) {
        try {
            getService().confirm(tx);
        } catch (RuntimeException ex) {
            log.error("async handel error -> {}", tx);
            log.error("async handel error", ex);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handle(TurntableWinning winning) {
        // 更新状态为已发放
        if (!updateWinningState(winning.getId(), 0, 1)) {
            log.error("奖品状态不匹配， 不做处理, [ id = {} ]", winning.getId());
            return;
        }

        long rewardId = configService.getConfig(BtBankSystemConfig.TURNTABLE_REWARD_ACCOUNT, (v) -> Long.parseLong(v.toString()), 0L);
        if (rewardId == 0) {
            log.error("未配置奖励发放账户 TURNTABLE_REWARD_ACCOUNT");
            throw new IllegalArgumentException("未配置奖励发放账户 TURNTABLE_REWARD_ACCOUNT");
        }

        BigDecimal amount = new BigDecimal(winning.getPrizeAmount()).negate();


        WalletChangeRecord record = memberWalletService.tryTrade(
                TransactionType.ACTIVITY_AWARD,
                rewardId,
                "BT",
                "BT",
                amount,
                winning.getId(),
                "幸运大转盘奖励扣除");
        if (record == null) {
            log.error("扣除发放奖励账户余额失败 [ id = {} ]", winning.getId());
            throw new IllegalArgumentException("扣除发放奖励账户余额失败");
        }

        try {
            if (winningTransactionService.saveRecord(winning.getId(), winning.getMemberId(), rewardId, amount.abs(), record.getId() + "")) {
                if (!memberWalletService.confirmTrade(rewardId, record.getId())) {
                    log.error("确认账户变动失败 [ id = {}, record = {} ]", winning.getId(), record);
                    throw new BtBankException(CommonMsgCode.FAILURE);
                }

                TurntableWinningTransaction tx = new TurntableWinningTransaction();
                tx.setId(winning.getId());
                tx.setMemberId(winning.getMemberId());
                tx.setAmount(amount.abs());

                getService().async(tx);

            } else {
                log.error("保存发放交易记录 [ id = {}, record = {} ] 失败", winning.getId(), record);
                throw new BtBankException(CommonMsgCode.FAILURE);
            }

        } catch (RuntimeException ex) {
            log.error("奖品发放处理失败 txId = {}, err = {}", winning.getId(), ex.getMessage());
            boolean b = memberWalletService.rollbackTrade(rewardId, record.getId());
            log.info("回滚账户变动 result = {}, record = {}", b, record);
            throw ex;
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void confirm(TurntableWinningTransaction tx) {
        // 更新状态为已发放
        if (!updateWinningState(tx.getId(), 1, 2)) {
            log.error("奖品状态不匹配， 不做处理, [ id = {} ]", tx.getId());
            return;
        }

        // 更新状态未转入中
        if (!updateWinningTransState(tx.getId(), 0, 1)) {
            log.error("奖品发放交易状态不匹配， 中断处理, [ id = {} ]", tx.getId());
            throw new IllegalArgumentException("奖品发放交易状态不匹配， 中断处理");
        }

        // 尝试加帐
        WalletChangeRecord record = memberWalletService.tryTrade(
                TransactionType.ACTIVITY_AWARD,
                tx.getMemberId(), "BT", "BT", tx.getAmount().abs(), tx.getId(),
                "幸运大转盘奖励");

        if (record == null) {
            log.error("奖品发放处理失败 txId = {}, member_id = {}, amount = {}", tx.getId(), tx.getMemberId(), tx.getAmount().abs());
            throw new IllegalArgumentException("奖品发放处理失败");
        }

        try {
            if (winningTransactionService.confirm(tx, record.getId() + "")) {
                // 确认账户
                boolean b = memberWalletService.confirmTrade(tx.getMemberId(), record.getId());
                if (!b) {
                    log.error("确认账户变动失败 record = {}", record);
                    throw new BtBankException(CommonMsgCode.FAILURE);
                }
            } else {
                log.error("奖品发放已被处理 txId = {}", tx.getId());
                throw new BtBankException(CommonMsgCode.FAILURE);
            }
        } catch (RuntimeException ex) {
            log.error("奖品发放处理失败 txId = {}, err = {}", tx.getId(), ex.getMessage());
            boolean b = memberWalletService.rollbackTrade(tx.getMemberId(), record.getId());
            log.info("回滚账户变动 result = {}, record = {}", b, record);
            throw ex;
        }
    }

    @Override
    public void giveOut(TurntableWinning winning) {
        if (!"BT".equals(winning.getPrizeType())) {
            updateWinningState(winning.getId(), 0, 1);
        }

        // 未自动发放的
        if (StatusUtils.equals(0, winning.getState())) {
            getService().async(winning);
            return;
        }

        // 补偿加帐
        TurntableWinningTransaction tx = winningTransactionService.getById(winning.getId());
        if (StatusUtils.equals(0, tx.getState())) {
            getService().async(tx);
        }
    }

    public TurntableGiveOutService getService() {
        return SpringContextUtil.getBean(TurntableGiveOutService.class);
    }

    private boolean updateWinningState(Long id, Integer source, Integer target) {
        UpdateWrapper<TurntableWinning> wrapper = new UpdateWrapper<>();

        wrapper.eq("id", id).eq("state", source).eq("prize_type", "BT")
                .set("state", target).set("update_time", new Date());

        return winningService.update(wrapper);
    }

    private boolean updateWinningTransState(Long id, Integer source, Integer target) {
        UpdateWrapper<TurntableWinningTransaction> wrapper = new UpdateWrapper<>();

        wrapper.eq("id", id).eq("state", source).eq("coin_unit", "BT")
                .set("state", target).set("update_time", new Date());

        return winningTransactionService.update(wrapper);
    }

}
