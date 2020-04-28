package com.spark.bitrade.biz.impl;

import com.spark.bitrade.api.dto.MemberRateDto;
import com.spark.bitrade.biz.MemberScoreBizService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.entity.MemberTransaction;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.enums.MessageCode;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.*;
import com.spark.bitrade.repository.service.*;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.service.IMemberTransactionService;
import com.spark.bitrade.service.IMemberWalletService;
import com.spark.bitrade.util.DateUtil;
import com.spark.bitrade.util.DateUtils;
import com.spark.bitrade.util.IdWorkByTwitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class MemberScoreBizServiceImpl implements MemberScoreBizService {

    @Autowired
    private BtBankMemberScoreWalletService btBankMemberScoreWalletService;
    @Autowired
    private BtBankScoreTransactionService btBankScoreTransactionService;
    @Autowired
    private BtBankMemberPendingWardService btBankMemberPendingWardService;
    @Autowired
    private BtBankConfigService configService;
    @Autowired
    private IMemberTransactionService memberTransactionService;
    @Autowired
    private IMemberWalletService memberWalletService;
    @Autowired
    private IdWorkByTwitter idWorkByTwitter;

    @Autowired
    private BtBankRebateRecordService rebateRecordService;
    @Autowired
    BtBankMinerBalanceTransactionService minerBalanceTransactionService;
    @Autowired
    private BtBankMinerBalanceService btBankMinerBalanceService;
    @Autowired
    private FinancialActivityJoinDetailsService financialActivityJoinDetailsService;
    @Value("${sourceMemberId:70653}")
    private Long sourceMemberId;

    @Override
    public void increaseScore(Long memberId, BigDecimal score, int type) {
        Date time = configService.getConfig(BtBankSystemConfig.SILVER_CREATION_REGISTER_TIME_END, (v) -> DateUtils.parseDatetime(v.toString()), DateUtils.parseDatetime("2020-04-01 00:00:00"));
        //在此配置时间之后 才能获得积分
        if (new Date().before(time)) {
            return;
        }

        BtBankMemberScoreWallet wallet = btBankMemberScoreWalletService.findOne(memberId);
        boolean update = btBankMemberScoreWalletService.lambdaUpdate().setSql("balance=balance+" + score.toPlainString())
                .eq(BtBankMemberScoreWallet::getId, wallet.getId()).update();
        Assert.isTrue(update, "积分增加失败");
        //保存流水
        BtBankScoreTransaction transaction = new BtBankScoreTransaction();
        transaction.setMemberId(memberId);
        transaction.setType(type);
        transaction.setChangeScore(score);
        btBankScoreTransactionService.save(transaction);


    }

    /**
     * 生成直推奖励 待领取
     *
     * @return
     */
    @Override
    public boolean addPendingRecord(Long memberId, BigDecimal amount, Long childId, int type, Long txId, String comment) {

        BtBankMemberPendingWard ward = new BtBankMemberPendingWard();
        ward.setMemberId(memberId);
        ward.setWardAmount(amount);
        ward.setStatus(0);
        ward.setChildId(childId);
        ward.setType(type);
        ward.setTxId(txId);
        ward.setComments(comment);

        return btBankMemberPendingWardService.save(ward);
    }

    @Override
    public MemberRateDto aprilOneRate(Date registerTime) {
        MemberRateDto dto = new MemberRateDto();
        Date time = configService.getConfig(BtBankSystemConfig.SILVER_CREATION_REGISTER_TIME_END, (v) -> DateUtils.parseDatetime(v.toString()), DateUtils.parseDatetime("2020-04-01 00:00:00"));
        if (registerTime.before(time)) {
            dto.setComment("");
            return dto;
        }

        String rateStr = (String) configService.getConfig(BtBankSystemConfig.APRIL_ONE_REGISTER_30_31_60_PROFIT_RATE);
        if (!StringUtils.hasLength(rateStr)) {
            rateStr = "1,0.5";
        }
        String[] arr = rateStr.split(",");

        Long l = DateUtil.diffDays(registerTime, new Date());
        l = new BigDecimal(l).abs().longValue();
        if (l <= 30) {
            dto.setComment("(前30天收益)");
            dto.setRate(new BigDecimal(arr[0]));
        } else if (l <= 60) {
            dto.setComment("(31-60天收益)");
            dto.setRate(new BigDecimal(arr[1]));
        } else {
            dto.setComment("");
        }
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doReceive(Long memberId, BtBankMemberPendingWard ward) {
        if (ward.getStatus() == 1) {
            throw new BtBankException(BtBankMsgCode.SCORE_HAS_RECEIVED);
        }
        Date now = new Date();
        String comment = ward.getComments();
        TransactionType transactionType = TransactionType.DIRECT_MINER_REWARD;
        if (ward.getType() == 1) {
            transactionType = TransactionType.FINANCIAL_ACTIVITY_RECOMMEND_REWARD;
        }
        if (ward.getType() == 2) {
            transactionType = TransactionType.WELFARE_NEW_PACKET_REWARD;
        }
        if (ward.getType() == 3) {
            transactionType = TransactionType.WELFARE_INCR_PACKET_REWARD;
        }
        //扣除总账户
        MemberWallet memberWallet = memberWalletService.findByCoinAndMemberId("BT", sourceMemberId);
        memberWalletService.trade(memberWallet.getId(), ward.getWardAmount().negate(), BigDecimal.ZERO, BigDecimal.ZERO);
        MemberTransaction transaction = new MemberTransaction();
        transaction.setAmount(ward.getWardAmount().negate());
        transaction.setCreateTime(new Date());
        transaction.setMemberId(sourceMemberId);
        transaction.setSymbol("BT");
        transaction.setType(transactionType);
        transaction.setFee(BigDecimal.ZERO);
        transaction.setFlag(0);
        transaction.setRefId("");
        transaction.setComment(comment);
        memberTransactionService.save(transaction);

        //增加用户余额
        MemberWallet wa = memberWalletService.findByCoinAndMemberId("BT", memberId);
        memberWalletService.trade(wa.getId(), ward.getWardAmount(), BigDecimal.ZERO, BigDecimal.ZERO);
        MemberTransaction transaction1 = new MemberTransaction();
        transaction1.setAmount(ward.getWardAmount());
        transaction1.setCreateTime(new Date());
        transaction1.setMemberId(memberId);
        transaction1.setSymbol("BT");
        transaction1.setType(transactionType);
        transaction1.setFee(BigDecimal.ZERO);
        transaction1.setFlag(0);
        transaction1.setRefId("");
        transaction1.setComment(comment);
        memberTransactionService.save(transaction1);

        //扣除积分
        boolean decreaseScore = btBankMemberScoreWalletService.decreaseScore(memberId, ward.getWardAmount());
        if (!decreaseScore) {
            throw new BtBankException(BtBankMsgCode.SCORE_INSUFFICIENT_CREDIT_BALANCE);
        }
        //更新状态为已领取
        boolean update = btBankMemberPendingWardService.lambdaUpdate()
                .set(BtBankMemberPendingWard::getStatus, 1)
                .set(BtBankMemberPendingWard::getReceiveTime, now)
                .eq(BtBankMemberPendingWard::getId, ward.getId()).update();
        if (!update) {
            throw new BtBankException(MessageCode.UNKNOW_ERROR);
        }
        if (ward.getType() == 0 && ward.getTxId() != null) {
            BtBankMinerBalanceTransaction trans = minerBalanceTransactionService.getById(ward.getTxId());
            // 保存奖励流水
            BtBankRebateRecord rebateRecord = new BtBankRebateRecord();
            rebateRecord.setId(idWorkByTwitter.nextId());
            rebateRecord.setMinerBalanceTransactionId(Optional.ofNullable(ward.getTxId()).orElse(0L));
            rebateRecord.setMinerMemberId(ward.getChildId());
            if (trans != null) {
                rebateRecord.setMinerRewardType(trans.getType());
                rebateRecord.setRewardAmount(trans.getMoney());
            }
            rebateRecord.setRebateAmount(ward.getWardAmount());
            rebateRecord.setRebateMemberId(ward.getMemberId());
            rebateRecord.setRebateLevel(1);
            rebateRecord.setRebateType(0);

            rebateRecord.setCreateTime(now);
            rebateRecord.setUpdateTime(now);
            if (!rebateRecordService.save(rebateRecord)) {
                throw new BtBankException(BtBankMsgCode.RELEASE_REWARD_FAILED);
            }

            // bt_bank_miner_balance 维护got_shared_reward_sum
            boolean rewardResult = btBankMinerBalanceService.lambdaUpdate()
                    .setSql("got_shared_reward_sum = got_shared_reward_sum + " + ward.getWardAmount())
                    .eq(BtBankMinerBalance::getMemberId, ward.getMemberId())
                    .update();
            if (!rewardResult) {
                log.warn("got_shared_reward_sum failed. memberId({}) amount({})", ward.getMemberId(),  ward.getWardAmount());
                throw new BtBankException(MessageCode.INVALID_ACCOUNT);
            }
        }
        if (ward.getType() == 1 && ward.getTxId() != null) {

            Boolean isUnlock = financialActivityJoinDetailsService.lambdaUpdate()
                    .set(FinancialActivityJoinDetails::getIsReceive, 1)
                    .set(FinancialActivityJoinDetails::getRecommendReleaseTime, now)
                    .eq(FinancialActivityJoinDetails::getId, ward.getTxId()).update();
            if (!isUnlock) {
                throw new BtBankException(BtBankMsgCode.RELEASE_REWARD_FAILED);
            }
        }
    }
}









