package com.spark.bitrade.biz.impl;

import com.spark.bitrade.api.vo.EnterpriseOrderVo;
import com.spark.bitrade.biz.EnterpriseMiningService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.constant.EnterpriseTransactionType;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.EnterpriseMinerTransaction;
import com.spark.bitrade.repository.service.EnterpriseMinerTransactionService;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.SpringContextUtil;
import com.spark.bitrade.util.StatusUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * EnterpriseMiningServiceImpl
 *
 * @author biu
 * @since 2019/12/24 13:41
 */
@Service
@Slf4j
@AllArgsConstructor
public class EnterpriseMiningServiceImpl implements EnterpriseMiningService {

    private EnterpriseMinerTransactionService transactionService;
    private BtBankConfigService configService;
    private MemberWalletService walletService;

    @Override
    public EnterpriseMinerTransaction mining(EnterpriseOrderVo orderVo) {

        if (transactionService.isExistOrder(orderVo.getOrderSn())) {
            throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_ORDER_EXISTS);
        }

        EnterpriseMinerTransaction tx = transactionService.mining(
                orderVo.getMinerId(),
                orderVo.getAmount(),
                orderVo.getOrderSn());

        log.info("挖矿处理成功 vo = {}", orderVo);
        // 进入奖励结算流程
        getService().handle(tx);
        return tx;
    }

    @Async
    @Override
    public void handle(EnterpriseMinerTransaction tx) {

        EnterpriseTransactionType type = EnterpriseTransactionType.of(tx.getType());

        // 挖矿订单
        if (type == EnterpriseTransactionType.MiningOrder && StatusUtils.equals(0, tx.getStatus())) {
            getService().collect(tx);
            return;
        }

        // 挖矿订单
        if (type == EnterpriseTransactionType.MiningOrder && StatusUtils.equals(1, tx.getStatus())) {
            if (StatusUtils.equals(0, tx.getRewardStatus())) {
                getService().reward(tx);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void collect(EnterpriseMinerTransaction tx) {

        // 尝试修改DB状态
        boolean notProcessed = transactionService.isNotProcessed(tx);

        if (!notProcessed) {
            log.info("txId = {} 归集已处理", tx.getId());
            return;
        }

        // 归集到账户
        Long memberId = configService.getConfig(BtBankSystemConfig.ENTERPRISE_MINER_RECEIVE_ACCOUNT, v -> Long.parseLong(v.toString()), 0L);
        if (memberId == 0) {
            throw new IllegalArgumentException("未找到归集账户 ENTERPRISE_MINER_RECEIVE_ACCOUNT 配置");
        }

        // 尝试加帐
        WalletChangeRecord record = walletService.tryTrade(
                TransactionType.ENTERPRISE_MINER_COLLECT,
                memberId, "BT", "BT", tx.getAmount().abs(), tx.getId(),
                "企业矿工挖矿归集");

        if (record == null) {
            log.error("归集处理失败 txId = {}, member_id = {}, amount = {}", tx.getId(), tx.getMemberId(), tx.getAmount().abs());
            throw new IllegalArgumentException("归集处理失败");
        }

        try {
            if (transactionService.collect(tx, record.getId())) {
                // 确认账户
                boolean b = walletService.confirmTrade(memberId, record.getId());
                if (!b) {
                    log.error("确认账户变动失败 record = {}", record);
                    throw new BtBankException(CommonMsgCode.FAILURE);
                } else {
                    // 处理奖励
                    getService().handle(transactionService.getById(tx.getId()));
                    return;
                }
            }
            log.error("归集已被处理 txId = {}", tx.getId());
            throw new BtBankException(CommonMsgCode.FAILURE);
        } catch (RuntimeException ex) {
            log.error("归集处理失败 txId = {}, err = {}", tx.getId(), ex.getMessage());
            boolean b = walletService.rollbackTrade(memberId, record.getId());
            log.info("回滚账户变动 result = {}, record = {}", b, record);
            throw ex;
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void reward(EnterpriseMinerTransaction tx) {

        EnterpriseMinerTransaction reward = transactionService.preReward(tx);

        // 奖励发放账户
        Long memberId = configService.getConfig(BtBankSystemConfig.ENTERPRISE_MINER_COMMISSION_ACCOUNT, v -> Long.parseLong(v.toString()), 0L);
        if (memberId == 0) {
            throw new IllegalArgumentException("未找到奖励发放账户 ENTERPRISE_MINER_COMMISSION_ACCOUNT 配置");
        }

        // 尝试扣款
        WalletChangeRecord record = walletService.tryTrade(
                TransactionType.ENTERPRISE_MINER_REWARD,
                memberId, "BT", "BT", reward.getReward().negate(),
                reward.getId(), "企业矿工挖矿奖励");


        if (record == null) {
            log.error("奖励发放处理失败 txId = {}, member_id = {}, amount = {}", reward.getId(), reward.getMemberId(), reward.getAmount().negate());
            throw new IllegalArgumentException("奖励发放处理");
        }

        try {
            if (transactionService.confirmReward(reward, record.getId())) {
                if (!walletService.confirmTrade(memberId, record.getId())) {
                    log.error("确认账户变动失败 record = {}", record);
                    throw new BtBankException(CommonMsgCode.FAILURE);
                }
            }
        } catch (RuntimeException ex) {
            log.error("奖励发放处理失败 txId = {}, err = {}", tx.getId(), ex.getMessage());
            boolean b = walletService.rollbackTrade(memberId, record.getId());
            log.info("回滚账户变动 result = {}, record = {}", b, record);
            throw ex;
        }

    }

    private EnterpriseMiningService getService() {
        return SpringContextUtil.getBean(EnterpriseMiningService.class);
    }
}
