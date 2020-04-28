package com.spark.bitrade.service;

import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constant.WalletChangeType;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.UnConfirmVo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Service
public class MemberWalletService {
    @Autowired
    private IMemberWalletApiService memberWalletApiService;
    @Autowired
    private IMemberTransactionApiService memberTransactionApiService;

    /**
     * 远程操作钱包余额
     *
     * @param memberId
     * @param coinId
     * @param unit
     * @param balance
     * @param minerTxId
     * @return com.spark.bitrade.util.MessageRespResult
     * @author zhangYanjun
     * @time 2019.10.02 20:40
     */
    public MessageRespResult optionMemberWalletBalance(TransactionType transactionType,
                                                       Long memberId,
                                                       String coinId,
                                                       String unit,
                                                       BigDecimal balance,
                                                       Long minerTxId,
                                                       String comment) {
        WalletTradeEntity tradeEntity = buildEntity(transactionType, memberId, coinId, unit, balance, minerTxId, comment);

        return memberWalletApiService.trade(tradeEntity);
    }

    /**
     * 远程操作钱包余额
     *
     * @param memberId
     * @param coinId
     * @param unit
     * @param balance
     * @param minerTxId
     * @return com.spark.bitrade.util.MessageRespResult
     * @author zhangYanjun
     * @time 2019.10.02 20:40
     */
    public MessageRespResult optionMemberWalletBalance(TransactionType transactionType,
                                                       Long memberId,
                                                       String coinId,
                                                       String unit,
                                                       BigDecimal balance,
                                                       BigDecimal lockBalance,
                                                       Long minerTxId,
                                                       String comment) {
        WalletTradeEntity tradeEntity = buildEntity(transactionType, memberId, coinId, unit, balance,lockBalance, minerTxId, comment);

        return memberWalletApiService.trade(tradeEntity);
    }

    /**
     * 冻结余额
     *
     * @param type      交易类型
     * @param memberId  会员ID
     * @param coinId    币种ID
     * @param unit      币种类型
     * @param amount    冻结数量
     * @param minimum   最小数量
     * @param minerTxId 关联id
     * @param comment   备注
     * @return resp
     */
    public MessageRespResult<Boolean> freeze(TransactionType type,
                                             Long memberId,
                                             String coinId,
                                             String unit,
                                             BigDecimal amount,
                                             BigDecimal minimum,
                                             Long minerTxId,
                                             String comment) {

        WalletTradeEntity tradeEntity = buildEntity(type,
                memberId, coinId, unit, amount.negate(), minerTxId, comment);
        // 冻结+
        tradeEntity.setTradeFrozenBalance(amount.abs());


        return memberWalletApiService.trade2(tradeEntity, minimum);
    }
    /**
     * 冻结余额
     *
     * @param type      交易类型
     * @param memberId  会员ID
     * @param coinId    币种ID
     * @param unit      币种类型
     * @param amount    冻结数量
     * @param minerTxId 关联id
     * @param comment   备注
     * @return resp
     */
    public WalletChangeRecord freeze(TransactionType type,
                                             Long memberId,
                                             String coinId,
                                             String unit,
                                             BigDecimal amount,
                                             Long minerTxId,
                                             String comment) {

        WalletTradeEntity tradeEntity = buildEntity(type,
                memberId, coinId, unit, amount.abs().negate(), minerTxId, comment);
        // 冻结+
        tradeEntity.setTradeFrozenBalance(amount.abs());

        MessageRespResult<WalletChangeRecord> respResult = memberWalletApiService.tradeTccTry(tradeEntity);
        if (respResult.isSuccess()) {
            WalletChangeRecord data = respResult.getData();
            return data;
        }

        return null;
    }

    /**
     * 冻结余额
     *
     * @param type      交易类型
     * @param memberId  会员ID
     * @param coinId    币种ID
     * @param unit      币种类型
     * @param amount    冻结数量
     * @param minerTxId 关联id
     * @param comment   备注
     * @return resp
     */
    public WalletChangeRecord rewardFreeze(TransactionType type,
                                     Long memberId,
                                     String coinId,
                                     String unit,
                                     BigDecimal amount,
                                     Long minerTxId,
                                     String comment) {

        WalletTradeEntity tradeEntity = buildEntity(type,
                memberId, coinId, unit, amount.abs().negate(), minerTxId, comment);
        // 冻结+
        tradeEntity.setTradeFrozenBalance(amount.abs());
        tradeEntity.setTradeBalance(BigDecimal.ZERO);
        MessageRespResult<WalletChangeRecord> respResult = memberWalletApiService.tradeTccTry(tradeEntity);
        if (respResult.isSuccess()) {
            WalletChangeRecord data = respResult.getData();
            return data;
        }

        return null;
    }
    /**
     * 直接扣冻结余额
     *
     * @param type      交易类型
     * @param memberId  会员ID
     * @param coinId    币种ID
     * @param unit      币种类型
     * @param amount    冻结数量
     * @param minerTxId 关联id
     * @param comment   备注
     * @return resp
     */
    public WalletChangeRecord drawFreeze(TransactionType type,
                                     Long memberId,
                                     String coinId,
                                     String unit,
                                     BigDecimal amount,
                                     Long minerTxId,
                                     String comment) {

        WalletTradeEntity tradeEntity = buildEntity(type,
                memberId, coinId, unit, BigDecimal.ZERO, minerTxId, comment);
        // 冻结减
        tradeEntity.setTradeFrozenBalance(amount.abs().negate());
        MessageRespResult<WalletChangeRecord> respResult = memberWalletApiService.tradeTccTry(tradeEntity);
        if (respResult.isSuccess()) {
            WalletChangeRecord data = respResult.getData();
            return data;
        }

        return null;
    }
    /**
     * 冻结余额释放到可用
     *
     * @param type      交易类型
     * @param memberId  会员ID
     * @param coinId    币种ID
     * @param unit      币种类型
     * @param amount    冻结数量
     * @param minerTxId 关联id
     * @param comment   备注
     * @return resp
     */
    public WalletChangeRecord realseFreeze(TransactionType type,
                                             Long memberId,
                                             String coinId,
                                             String unit,
                                             BigDecimal amount,
                                             Long minerTxId,
                                             String comment) {

        WalletTradeEntity tradeEntity = buildEntity(type,
                memberId, coinId, unit, amount.abs(), minerTxId, comment);
        // 冻结+
        tradeEntity.setTradeFrozenBalance(amount.abs().negate());
        MessageRespResult<WalletChangeRecord> respResult = memberWalletApiService.tradeTccTry(tradeEntity);
        if (respResult.isSuccess()) {
            WalletChangeRecord data = respResult.getData();
            return data;
        }

        return null;
    }
    /**
     * TCC模式 预执行计划
     *
     * @param transactionType
     * @param memberId
     * @param coinId
     * @param unit
     * @param balance
     * @param minerTxId
     * @param comment
     * @return
     */
    public WalletChangeRecord tryTrade(TransactionType transactionType,
                                       Long memberId,
                                       String coinId,
                                       String unit,
                                       BigDecimal balance,
                                       Long minerTxId,
                                       String comment) {
        WalletTradeEntity tradeEntity = buildEntity(transactionType, memberId, coinId, unit, balance, minerTxId, comment);
        MessageRespResult<WalletChangeRecord> respResult = memberWalletApiService.tradeTccTry(tradeEntity);
        if (respResult.isSuccess()) {
            WalletChangeRecord data = respResult.getData();
            return data;
        }

        return null;
    }

    /**
     * TCC模式 提交计划
     *
     * @param memberId
     * @param tradeRecordId
     * @return
     */
    public boolean confirmTrade(Long memberId, Long tradeRecordId) {
        MessageRespResult<Boolean> respResult = memberWalletApiService.tradeTccConfirm(memberId, tradeRecordId);
        if (!respResult.isSuccess()) {
            return false;
        }
        return respResult.getData();
    }

    /**
     * TCC模式 回滚计划
     *
     * @param memberId
     * @param tradeRecordId
     * @return
     */
    public boolean rollbackTrade(Long memberId, Long tradeRecordId) {
        MessageRespResult<Boolean> respResult = memberWalletApiService.tradeTccCancel(memberId, tradeRecordId);
        if (!respResult.isSuccess()) {
            return false;
        }
        return respResult.getData();
    }

    /**
     * TCC模式 批量提交执行计划
     *
     * @param plan
     * @return
     */
    public boolean confirmPlan(TradePlan plan) {
        List<Boolean> collect = plan.queue.stream()
                .map(x -> this.confirmTrade(x.getMemberId(), x.getId()))
                .collect(Collectors.toList());
        Optional<Boolean> any = collect.stream().filter(x -> !x).findAny();

        return any.orElse(Boolean.TRUE);
    }

    /**
     * TCC模式 批量回滚执行计划
     *
     * @param plan
     * @return
     */
    public boolean rollbackPlan(TradePlan plan) {
        List<Boolean> collect = plan.queue.stream()
                .map(x -> this.rollbackTrade(x.getMemberId(), x.getId()))
                .collect(Collectors.toList());
        Optional<Boolean> any = collect.stream().filter(x -> !x).findAny();
        return any.orElse(Boolean.TRUE);
    }

    /**
     * 未提交的记录
     *
     * @return records
     */
    public MessageRespResult<List<UnConfirmVo>> unConfirmRecords() {
        return memberTransactionApiService.unConfirmWalletChangeRecords();
    }

    /**
     * 提交记录
     *
     * @param vo vo
     * @return resp
     */
    public MessageRespResult<Boolean> doConfirm(UnConfirmVo vo) {
        return memberWalletApiService.tradeTccConfirm(vo.getMemberId(), vo.getId());
    }

    @Data
    public static class TradePlan {
        ConcurrentLinkedQueue<WalletChangeRecord> queue = new ConcurrentLinkedQueue<>();
    }

    private WalletTradeEntity buildEntity(TransactionType transactionType,
                                          Long memberId,
                                          String coinId,
                                          String unit,
                                          BigDecimal balance,
                                          BigDecimal lockBalance,
                                          Long minerTxId,
                                          String comment) {
        WalletTradeEntity tradeEntity = new WalletTradeEntity();
        tradeEntity.setType(transactionType);
        tradeEntity.setRefId(minerTxId.toString());
        tradeEntity.setChangeType(WalletChangeType.TRADE);
        tradeEntity.setMemberId(memberId);
        tradeEntity.setCoinId(coinId);
        tradeEntity.setCoinUnit(unit);
        tradeEntity.setTradeBalance(balance);
        tradeEntity.setTradeLockBalance(lockBalance);
        tradeEntity.setComment(comment);
        return tradeEntity;
    }

    private WalletTradeEntity buildEntity(TransactionType transactionType,
                                          Long memberId,
                                          String coinId,
                                          String unit,
                                          BigDecimal balance,
                                          Long minerTxId,
                                          String comment) {
        WalletTradeEntity tradeEntity = new WalletTradeEntity();
        tradeEntity.setType(transactionType);
        tradeEntity.setRefId(minerTxId.toString());
        tradeEntity.setChangeType(WalletChangeType.TRADE);
        tradeEntity.setMemberId(memberId);
        tradeEntity.setCoinId(coinId);
        tradeEntity.setCoinUnit(unit);
        tradeEntity.setTradeBalance(balance);
        tradeEntity.setComment(comment);
        return tradeEntity;
    }

    /**
     * 未提交的记录
     *
     * @return records
     */
    public MessageRespResult<MemberWallet> getWalletByUnit(Long memberId,String coinId) {
        return memberWalletApiService.getWalletByUnit(memberId,coinId);
    }
}
