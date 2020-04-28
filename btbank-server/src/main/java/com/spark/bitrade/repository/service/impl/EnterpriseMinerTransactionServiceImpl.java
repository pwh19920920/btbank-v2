package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.constant.EnterpriseTransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.EnterpriseMiner;
import com.spark.bitrade.repository.entity.EnterpriseMinerTransaction;
import com.spark.bitrade.repository.mapper.EnterpriseMinerTransactionMapper;
import com.spark.bitrade.repository.service.EnterpriseMinerService;
import com.spark.bitrade.repository.service.EnterpriseMinerTransactionService;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.util.DateUtils;
import com.spark.bitrade.util.StatusUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * 企业矿工流水表(EnterpriseMinerTransaction)表服务实现类
 *
 * @author biu
 * @since 2019-12-23 17:14:35
 */
@Service("enterpriseMinerTransactionService")
public class EnterpriseMinerTransactionServiceImpl extends ServiceImpl<EnterpriseMinerTransactionMapper, EnterpriseMinerTransaction> implements EnterpriseMinerTransactionService {

    private EnterpriseMinerService minerService;
    private BtBankConfigService configService;

    @Autowired
    public void setMinerService(EnterpriseMinerService minerService) {
        this.minerService = minerService;
    }

    @Autowired
    public void setConfigService(BtBankConfigService configService) {
        this.configService = configService;
    }

    @Override
    public BigDecimal sumRewardOfYesterday(Long memberId) {
        Calendar yesterday = DateUtils.getCalendarOfYesterday();

        Date start = yesterday.getTime();
        yesterday.add(Calendar.DATE, 1);
        Date end = yesterday.getTime();

        BigDecimal sumReward = baseMapper.sumReward(memberId, start, end);
        return sumReward == null ? BigDecimal.ZERO : sumReward;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public EnterpriseMinerTransaction preTransfer(Long memberId, BigDecimal amount) {

        EnterpriseMiner miner = minerService.findByMemberId(memberId);
        if (miner == null || !StatusUtils.equals(1, miner.getStatus())) {
            throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_UNAVAILABLE);
        }

        EnterpriseTransactionType type = EnterpriseTransactionType.None;

        EnterpriseMinerTransaction tx = new EnterpriseMinerTransaction();

        tx.setId(IdWorker.getId());
        tx.setMemberId(memberId);
        tx.setMinerId(miner.getId());

        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            type = EnterpriseTransactionType.TransferIn;
        } else {
            type = EnterpriseTransactionType.TransferOut;
        }

        tx.setType(type.code());
        tx.setAmount(amount);
        tx.setReward(BigDecimal.ZERO);
        tx.setStatus(BooleanEnum.IS_FALSE.ordinal());
        tx.setCreateTime(new Date());

        if (type == EnterpriseTransactionType.TransferOut) {
            boolean transfer = minerService.transfer(memberId, amount);
            if (!transfer) {
                throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_BALANCE_NOT_ENOUGH);
            }
        }

        if (save(tx)) {
            return tx;
        }
        throw new BtBankException(CommonMsgCode.FAILURE);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean confirmTransfer(EnterpriseMinerTransaction tx, Long recordId) {
        Date now = new Date();

        if (!EnterpriseTransactionType.isTransfer(tx.getType())) {
            throw new BtBankException(4001, "不支持的类型");
        }

        UpdateWrapper<EnterpriseMinerTransaction> update = new UpdateWrapper<>();
        update.eq("id", tx.getId()).eq("status", 0)
                .set("ref_id", recordId + "")
                .set("status", 1)
                .set("update_time", now);


        boolean u = update(update);

        if (EnterpriseTransactionType.of(tx.getType()) == EnterpriseTransactionType.TransferIn && u) {
            return minerService.transfer(tx.getMemberId(), tx.getAmount());
        }
        return u;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public EnterpriseMinerTransaction mining(Integer minerId, BigDecimal amount, String orderSn) {

        EnterpriseMiner miner = minerService.getById(minerId);

        if (miner == null || !StatusUtils.equals(1, miner.getStatus())) {
            throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_UNAVAILABLE);
        }

        // 扣除矿池余额
        if (!minerService.mining(minerId, amount)) {
            throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_BALANCE_NOT_ENOUGH);
        }

        EnterpriseMinerTransaction tx = new EnterpriseMinerTransaction();

        tx.setId(IdWorker.getId());
        tx.setMemberId(miner.getMemberId());
        tx.setMinerId(minerId);
        tx.setType(EnterpriseTransactionType.MiningOrder.code());
        tx.setAmount(amount.negate());
        tx.setOrderSn(orderSn);
        tx.setReward(BigDecimal.ZERO);
        tx.setStatus(BooleanEnum.IS_FALSE.ordinal());
        tx.setCreateTime(new Date());

        if (save(tx)) {
            return tx;
        }
        throw new BtBankException(CommonMsgCode.FAILURE);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public EnterpriseMinerTransaction preReward(EnterpriseMinerTransaction tx) {
        Date now = new Date();
        EnterpriseTransactionType type = EnterpriseTransactionType.of(tx.getType());

        if (type != EnterpriseTransactionType.MiningOrder) {
            throw new BtBankException(4001, "TX类型参数错误");
        }

        UpdateWrapper<EnterpriseMinerTransaction> update = new UpdateWrapper<>();
        update.eq("id", tx.getId()).eq("reward_status", 0)
                .set("reward_status", 1).set("update_time", now);

        if (!update(update)) {
            throw new BtBankException(4001, "奖励发放已预处理");
        }

        EnterpriseMinerTransaction rtx = new EnterpriseMinerTransaction();

        rtx.setId(IdWorker.getId());
        rtx.setMemberId(tx.getMemberId());
        rtx.setMinerId(tx.getMinerId());
        rtx.setType(EnterpriseTransactionType.MiningReward.code());
        rtx.setAmount(tx.getAmount());
        rtx.setOrderSn(tx.getOrderSn());

        BigDecimal rate = configService.getConfig(BtBankSystemConfig.ENTERPRISE_MINER_COMMISSION_RATE,
                v -> new BigDecimal(v.toString()), BigDecimal.ZERO);

        if (rate.compareTo(BigDecimal.ZERO) < 1) {
            throw new BtBankException(4001, "ENTERPRISE_MINER_COMMISSION_RATE 未配置");
        }

        // 奖励
        BigDecimal reward = tx.getAmount().abs().multiply(rate).setScale(8, BigDecimal.ROUND_DOWN);
        rtx.setReward(reward);

        rtx.setStatus(BooleanEnum.IS_FALSE.ordinal());
        rtx.setRewardStatus(BooleanEnum.IS_FALSE.ordinal());
        rtx.setCreateTime(now);

        if (save(rtx)) {
            return rtx;
        }
        throw new BtBankException(CommonMsgCode.FAILURE);
    }

    @Override
    public boolean isNotProcessed(EnterpriseMinerTransaction tx) {
        // 未处理过的
        QueryWrapper<EnterpriseMinerTransaction> query = new QueryWrapper<>();
        query.eq("id", tx.getId()).eq("status", 0);

        return count(query) == 1;
    }

    @Override
    public boolean collect(EnterpriseMinerTransaction tx, Long recordId) {
        Date now = new Date();
        EnterpriseTransactionType type = EnterpriseTransactionType.of(tx.getType());
        if (type != EnterpriseTransactionType.MiningOrder) {
            throw new BtBankException(4001, "TX类型参数错误");
        }

        UpdateWrapper<EnterpriseMinerTransaction> update = new UpdateWrapper<>();
        update.eq("id", tx.getId()).eq("status", 0).eq("type", type.code())
                .isNull("ref_id")
                .set("status", 1)
                .set("ref_id", recordId + "")
                .set("update_time", now);

        return update(update);
    }

    @Override
    public boolean confirmReward(EnterpriseMinerTransaction tx, Long recordId) {
        Date now = new Date();

        EnterpriseTransactionType type = EnterpriseTransactionType.of(tx.getType());
        if (type != EnterpriseTransactionType.MiningReward) {
            throw new BtBankException(4001, "TX类型参数错误");
        }

        UpdateWrapper<EnterpriseMinerTransaction> update = new UpdateWrapper<>();
        update.eq("id", tx.getId()).eq("status", 0)
                .set("ref_id", recordId + "")
                .set("status", 1)
                .set("update_time", now);


        boolean u = update(update);
        if (u && minerService.reward(tx.getMinerId(), tx.getReward())) {
            return true;
        }
        throw new BtBankException(CommonMsgCode.FAILURE);
    }

    @Override
    public boolean isExistOrder(String orderSn) {
        QueryWrapper<EnterpriseMinerTransaction> query = new QueryWrapper<>();
        query.eq("order_sn", orderSn).eq("type", EnterpriseTransactionType.MiningOrder.code());
        return count(query) > 0;
    }
}