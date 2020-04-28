package com.spark.bitrade.biz.impl;

import com.spark.bitrade.api.dto.MemberRateDto;
import com.spark.bitrade.biz.MemberScoreBizService;
import com.spark.bitrade.biz.MinerRebateService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.constant.MinerGrade;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.enums.MessageCode;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.BtBankMinerBalance;
import com.spark.bitrade.repository.entity.BtBankMinerBalanceTransaction;
import com.spark.bitrade.repository.entity.BtBankRebateRecord;
import com.spark.bitrade.repository.entity.FinancialActivityJoinDetails;
import com.spark.bitrade.repository.service.BtBankMinerBalanceService;
import com.spark.bitrade.repository.service.BtBankMinerBalanceTransactionService;
import com.spark.bitrade.repository.service.BtBankMinerGradeNoteService;
import com.spark.bitrade.repository.service.BtBankRebateRecordService;
import com.spark.bitrade.service.*;
import com.spark.bitrade.util.*;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 矿工返利服务
 */
@Slf4j
@Service
public class MinerRebateServiceImpl implements MinerRebateService {
    @Autowired
    private IdWorkByTwitter idWorkByTwitter;

    @Autowired
    BtBankRebateRecordService rebateRecordService;

    @Autowired
    BtBankMinerGradeNoteService gradeNoteService;

    @Autowired
    BtBankMinerBalanceTransactionService minerBalanceTransactionService;

    @Autowired
    MemberAccountService memberAccountService;

    @Autowired
    MemberWalletService memberWalletService;

    @Autowired
    BtBankConfigService configService;

    @Autowired
    BtBankMinerBalanceService minerBalanceService;
    @Autowired
    private MemberScoreBizService memberScoreBizService;
    @Autowired
    private IMemberWalletService localMemberWalletService;
    @Autowired
    private IMemberTransactionService memberTransactionService;
    @Value("${btbank.reward.member:70653}")
    private Long rewardMemberId;

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @Async
    @SneakyThrows
    @Override
    public void processRebate() {
        // 查询所有已经释放的奖励记录，type：4，7，9
        // 4 抢单佣金转出，7 派单佣金转出，9 固定佣金转出
        List<BtBankMinerBalanceTransaction> needRebateTx = minerBalanceTransactionService.listNeedRebate();
        needRebateTx.forEach(x -> {
            getService().doMinerReward(x);
        });
    }

    public void doMinerReward(BtBankMinerBalanceTransaction btBankMinerBalanceTransaction) {
        MemberWalletService.TradePlan plan = new MemberWalletService.TradePlan();
        try {
            SpringContextUtil.getBean(MinerRebateServiceImpl.class).processRebateRecord(btBankMinerBalanceTransaction, plan);
            if (plan.getQueue().size() > 0) {
                if (log.isInfoEnabled()) {
                    log.info("矿工邀请奖励释放完成，执行远程提交.提交总数 {} 计划:", plan.getQueue().size());
                    plan.getQueue().forEach(y -> {
                        log.info("memberId({}) amount({}) type({})", y.getMemberId(), y.getTradeBalance(), y.getType());
                    });
                }
                memberWalletService.confirmPlan(plan);
            }
        } catch (Exception e) {
            if (log.isInfoEnabled() && plan.getQueue().size() > 0) {
                log.info("矿工邀请奖励释放失败，执行远程回滚. 回滚总数({}) 计划:", plan.getQueue().size());
                plan.getQueue().forEach(y -> {
                    log.info("memberId({}) amount({}) type({})", y.getMemberId(), y.getTradeBalance(), y.getType());
                });
            }
            memberWalletService.rollbackPlan(plan);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void processRebateRecord(BtBankMinerBalanceTransaction tx, MemberWalletService.TradePlan plan) {
        // 检查用户是否存在邀请人
        Member member = memberAccountService.findMemberByMemberId(tx.getMemberId());
        if (Objects.isNull(member) || Objects.isNull(member.getInviterId())) {
            // 当前用户没有邀请人，直接标记已处理
            minerBalanceTransactionService.markRebateProcessedById(tx.getId());
            return;
        }

        // 处理银牌矿工直推奖励
        // getService().processRecommendedReward(tx, member.getInviterId(), plan);
        getService().processRecommendedReward(tx, member, plan);

        // 查询祖父级推荐人
        Member fatherMember = memberAccountService.findMemberByMemberId(member.getInviterId());
        if (Objects.nonNull(fatherMember) && Objects.nonNull(fatherMember.getInviterId())) {
            // 递归查询释放金牌矿工奖励
            getService().processSuperiorRewards(tx, fatherMember.getInviterId(), 2, plan);
        }

        // 标记奖励已经处理完成
        minerBalanceTransactionService.markRebateProcessedById(tx.getId());
    }

    /**
     * 大宗挖矿 金牌矿工释放奖励
     *
     * @param fi
     * @param fatherId
     */
    public void processFinancialSuperiorRewards(FinancialActivityJoinDetails fi, Long fatherId) {
        BtBankMinerBalance minerBalance = minerBalanceService.findFirstByMemberId(fatherId);
        if (Objects.nonNull(minerBalance) && minerBalance.getMinerGrade() == MinerGrade.GOLD_MINER.getGradeId()) {
            log.info("--------------------------符合条件的金牌ID:{}-------------------------------", fatherId);
            // 金牌矿工身份，释放奖励
            // 释放奖励给邀请者
            String comment = String.format("%s参与大宗挖矿金牌佣金奖励", fi.getMemberId());

            String commissionRateStr = (String) configService.getConfig(BtBankSystemConfig.GOLD_MINER_COMMISSION_RATE);
            if (Objects.isNull(commissionRateStr)) {
                log.warn("query MINER_COMMISSION_RATE config failed. Key:{}", BtBankSystemConfig.GOLD_MINER_COMMISSION_RATE);
                throw new BtBankException(BtBankMsgCode.FAILED_TO_LOAD_SYSTEM_CONFIGURATION);
            }
            BigDecimal commissionRate = new BigDecimal(commissionRateStr);
            //返佣奖励数量
            BigDecimal rewardAmount = BigDecimalUtils.mulDown(fi.getReleaseProfitAmount(), commissionRate, 2);
            if (BigDecimalUtil.lte0(rewardAmount)) {
                // 这里的 rewardAmount 在两位小数的情况下，可能直接等于 0，后面的逻辑没有意义
                return;
            }
            //查询总账户 扣除总账户
            MemberWallet totalWallet = localMemberWalletService.findByCoinAndMemberId("BT", rewardMemberId);
            localMemberWalletService.trade(totalWallet.getId(), rewardAmount.negate(), BigDecimal.ZERO, BigDecimal.ZERO);
            //资金流水
            memberTransactionService.createTransaction(rewardAmount.negate(), rewardMemberId, TransactionType.GOLDEN_MINER_REWARD, comment);

            //增加金牌用户奖励
            MemberWallet fatherWallet = localMemberWalletService.findByCoinAndMemberId("BT", fatherId);
            localMemberWalletService.trade(fatherWallet.getId(), rewardAmount, BigDecimal.ZERO, BigDecimal.ZERO);
            memberTransactionService.createTransaction(rewardAmount, fatherId, TransactionType.GOLDEN_MINER_REWARD, comment);

            // 保存奖励流水
            BtBankRebateRecord rebateRecord = new BtBankRebateRecord();
            rebateRecord.setId(idWorkByTwitter.nextId());
            rebateRecord.setMinerBalanceTransactionId(fi.getId());
            rebateRecord.setMinerMemberId(fi.getMemberId());
            rebateRecord.setMinerRewardType(13); // fixed 13大宗挖矿
            rebateRecord.setRewardAmount(rewardAmount);
            rebateRecord.setRebateAmount(rewardAmount);
            rebateRecord.setRebateMemberId(fatherId);
            rebateRecord.setRebateLevel(3);
            rebateRecord.setRebateType(1);
            rebateRecord.setRefId(fi.getId());
            Date now = new Date();
            rebateRecord.setCreateTime(now);
            rebateRecord.setUpdateTime(now);
            if (!rebateRecordService.save(rebateRecord)) {
                throw new BtBankException(BtBankMsgCode.RELEASE_REWARD_FAILED);
            }

            log.info("--------------------------符合条件的金牌ID:{},返佣成功-------------------------------", fatherId);
            // bt_bank_miner_balance 维护got_shared_reward_sum
            boolean rewardResult = minerBalanceService.lambdaUpdate()
                    .setSql("got_shared_reward_sum = got_shared_reward_sum + " + rewardAmount)
                    .eq(BtBankMinerBalance::getMemberId, fatherId)
                    .update();
            if (!rewardResult) {
                log.warn("got_shared_reward_sum failed. memberId({}) amount({})", fatherId, rewardAmount);
                throw new BtBankException(MessageCode.INVALID_ACCOUNT);
            }

            return;
        }
        // 递归查询金牌矿工身份释放金牌矿工奖励
        Member member = memberAccountService.findMemberByMemberId(fatherId);
        if (Objects.nonNull(member) && Objects.nonNull(member.getInviterId())) {
            getService().processFinancialSuperiorRewards(fi, member.getInviterId());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void processSuperiorRewards(BtBankMinerBalanceTransaction tx, Long inviterId, Integer level, MemberWalletService.TradePlan plan) {
        BtBankMinerBalance minerBalance = minerBalanceService.findFirstByMemberId(inviterId);
        if (Objects.nonNull(minerBalance) && minerBalance.getMinerGrade() == MinerGrade.GOLD_MINER.getGradeId()) {
            // 金牌矿工身份，释放奖励
            // 释放奖励给邀请者
            RebatePlan rebatePlan = new RebatePlan();
            rebatePlan.setTxId(tx.getId());
            rebatePlan.setMoney(tx.getMoney());
            rebatePlan.setCommissionRateKey(BtBankSystemConfig.GOLD_MINER_COMMISSION_RATE);
            rebatePlan.setComment(String.format("%s参与BT挖矿金牌佣金奖励", tx.getMemberId()));
            rebatePlan.setMinerMemberId(tx.getMemberId());
            rebatePlan.setType(tx.getType());
            rebatePlan.setRebateMemberId(inviterId);
            rebatePlan.setRebateLevel(level);
            rebatePlan.setRebateType(1);
            getService().dispatchReward(rebatePlan, plan);
            // MEMO: 2019-11-18 需求变更：金牌矿工返佣，只返回给上级第一个具有金牌矿工身份的人
            return;
        }

        // 递归查询金牌矿工身份释放金牌矿工奖励
        Member member = memberAccountService.findMemberByMemberId(inviterId);
        if (Objects.nonNull(member) && Objects.nonNull(member.getInviterId())) {
            getService().processSuperiorRewards(tx, member.getInviterId(), level + 1, plan);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    // public void processRecommendedReward(BtBankMinerBalanceTransaction tx, Long inviterId, MemberWalletService.TradePlan plan) {
    public void processRecommendedReward(BtBankMinerBalanceTransaction tx, Member member, MemberWalletService.TradePlan plan) {
        BtBankMinerBalance minerBalance = minerBalanceService.findFirstByMemberId(member.getInviterId());
        if (Objects.isNull(minerBalance) || minerBalance.getMinerGrade() < MinerGrade.SILVER_MINER.getGradeId()) {
            // 仅当推荐人具有矿工身份时，才释放直推奖励给邀请者
            return;
        }

        // 收益比例
        String commissionRateKey = BtBankSystemConfig.SILVER_MINER_COMMISSION_RATE;
        String comment = "直属矿工推荐奖励";
        // 判断结束时间，如果在结束之后注册的直接按照之前的收益，之前结束的的进入逻辑处理。
        //查询配置
        Date endTime = new Date();
        try {
            String endTimeStr = (String) configService.getConfig(BtBankSystemConfig.RECOMMEND_NEW_MINER_ENDTIME);
            if (StringUtils.isNotBlank(endTimeStr)) {
                endTime = simpleDateFormat.parse(endTimeStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (member.getRegistrationTime() != null && endTime != null && member.getRegistrationTime().getTime() <= endTime.getTime()) {
            if (configService.isNewMemberConfig(member)) {
                // 推荐新矿工注册后，新矿工7日内获得的挖矿收益，推荐人可获得100%的推荐收益
                Date registrationTime = member.getRegistrationTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(registrationTime);
                // 第三天开始释放奖励 7 + 2 = 9
                calendar.add(Calendar.DATE, 9);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                // 在这个时间范围内, 获取 100% 的收益
                if (tx.getCreateTime().compareTo(calendar.getTime()) <= 0) {
                    commissionRateKey = BtBankSystemConfig.RECOMMEND_NEW_MINER_COMMISSION_RATE;
                    comment += "(7日内新矿工挖矿收益)";
                }
            }
        }

        RebatePlan rebatePlan = new RebatePlan();
        rebatePlan.setTxId(tx.getId());
        rebatePlan.setMoney(tx.getMoney());
        rebatePlan.setCommissionRateKey(commissionRateKey);
        rebatePlan.setComment(comment);
        rebatePlan.setMinerMemberId(tx.getMemberId());
        rebatePlan.setType(tx.getType());
        rebatePlan.setRebateMemberId(member.getInviterId());
        rebatePlan.setRebateLevel(1);
        rebatePlan.setRebateType(0);

        Date time = configService.getConfig(BtBankSystemConfig.SILVER_CREATION_REGISTER_TIME_END, (v) -> DateUtils.parseDatetime(v.toString()), DateUtils.parseDatetime("2020-04-01 00:00:00"));
        if (new Date().before(time)) {
            getService().dispatchReward(rebatePlan, plan);
        }

        if (new Date().after(time)) {
            //不直接发放直推奖励 改为创建待领取
            getService().createPendingRecord(rebatePlan, member.getRegistrationTime());
        }


    }

    @Transactional(rollbackFor = Exception.class)
    public void createPendingRecord(RebatePlan rebatePlan, Date registerTime) {
        String commissionRateStr = (String) configService.getConfig(rebatePlan.getCommissionRateKey());
        if (Objects.isNull(commissionRateStr)) {
            log.warn("query MINER_COMMISSION_RATE config failed. Key:{}", rebatePlan.getCommissionRateKey());
            throw new BtBankException(BtBankMsgCode.FAILED_TO_LOAD_SYSTEM_CONFIGURATION);
        }

        BigDecimal commissionRate = new BigDecimal(commissionRateStr);
        //4月1日 0:00后推荐注册的有效矿工，可获得其每笔挖矿收益：前30天100%，第二个30天50%，然后恢复10%的奖励（大宗挖矿的直推奖励也按这个比例）
        MemberRateDto dto = memberScoreBizService.aprilOneRate(registerTime);
        if (dto.getRate() != null) {
            commissionRate = dto.getRate();
        }
        BigDecimal rewardAmount = BigDecimalUtils.mulDown(rebatePlan.getMoney(), commissionRate, 2);

        if (BigDecimalUtil.lte0(rewardAmount)) {
            // 这里的 rewardAmount 在两位小数的情况下，可能直接等于 0，后面的逻辑没有意义
            return;
        }
        boolean b = memberScoreBizService.addPendingRecord(rebatePlan.getRebateMemberId(), rewardAmount,
                rebatePlan.getMinerMemberId(), 0, rebatePlan.getTxId(), String.format("直属矿工推荐奖励%s", dto.getComment()));
        Assert.isTrue(b, "保存待领取记录失败");
    }


    @Transactional(rollbackFor = Exception.class)
    public void dispatchReward(RebatePlan rebatePlan, MemberWalletService.TradePlan plan) {
        String commissionRateStr = (String) configService.getConfig(rebatePlan.getCommissionRateKey());
        if (Objects.isNull(commissionRateStr)) {
            log.warn("query MINER_COMMISSION_RATE config failed. Key:{}", rebatePlan.getCommissionRateKey());
            throw new BtBankException(BtBankMsgCode.FAILED_TO_LOAD_SYSTEM_CONFIGURATION);
        }

        BigDecimal commissionRate = new BigDecimal(commissionRateStr);
        BigDecimal rewardAmount = BigDecimalUtils.mulDown(rebatePlan.getMoney(), commissionRate, 2);

        if (BigDecimalUtil.lte0(rewardAmount)) {
            // 这里的 rewardAmount 在两位小数的情况下，可能直接等于 0，后面的逻辑没有意义
            return;
        }
        // 账户可用增加资产
        TransactionType txType = rebatePlan.getRebateLevel() > 1 ? TransactionType.GOLDEN_MINER_REWARD : TransactionType.DIRECT_MINER_REWARD;
        // TCC 预扣除 rewardAmount 数量的奖励佣金
        WalletChangeRecord supplyRecord = memberWalletService.tryTrade(txType
                , rewardMemberId
                , "BT"
                , "BT"
                , rewardAmount.negate()
                , rebatePlan.getTxId()
                , rebatePlan.getComment());
        if (Objects.isNull(supplyRecord)) {
            // 预扣款失败
            log.info("memberWalletService.tryTrade failed. memberId({}) amount({}) TransactionType({})", rewardMemberId,
                    rewardAmount.negate(), txType);
            throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }
        plan.getQueue().add(supplyRecord);

        // 保存奖励流水
        BtBankRebateRecord rebateRecord = new BtBankRebateRecord();
        rebateRecord.setId(idWorkByTwitter.nextId());
        rebateRecord.setMinerBalanceTransactionId(rebatePlan.getTxId());
        rebateRecord.setMinerMemberId(rebatePlan.getMinerMemberId());
        rebateRecord.setMinerRewardType(rebatePlan.getType());
        rebateRecord.setRewardAmount(rebatePlan.getMoney());
        rebateRecord.setRebateAmount(rewardAmount);
        rebateRecord.setRebateMemberId(rebatePlan.getRebateMemberId());
        rebateRecord.setRebateLevel(rebatePlan.getRebateLevel());
        rebateRecord.setRebateType(rebatePlan.getRebateType());
        rebateRecord.setRefId(supplyRecord.getId());
        Date now = new Date();
        rebateRecord.setCreateTime(now);
        rebateRecord.setUpdateTime(now);
        if (!rebateRecordService.save(rebateRecord)) {
            throw new BtBankException(BtBankMsgCode.RELEASE_REWARD_FAILED);
        }

        log.debug("保存奖励释放流水记录：{}", rebateRecord);


        WalletChangeRecord chargeRecord = memberWalletService.tryTrade(txType,
                rebatePlan.getRebateMemberId(),
                "BT",
                "BT",
                rewardAmount,
                rebateRecord.getId(),
                rebatePlan.getComment());
        if (Objects.isNull(chargeRecord)) {
            // 预扣款失败
            log.info("memberWalletService.tryTrade Reward credit failed. memberId({}) amount({}) TransactionType({})", rewardMemberId,
                    rewardAmount, txType);
            throw new BtBankException(MessageCode.FAILED_ADD_BALANCE);
        }
        plan.getQueue().add(chargeRecord);

        // bt_bank_miner_balance 维护got_shared_reward_sum
        boolean rewardResult = minerBalanceService.lambdaUpdate()
                .setSql("got_shared_reward_sum = got_shared_reward_sum + " + rewardAmount)
                .eq(BtBankMinerBalance::getMemberId, rebatePlan.getRebateMemberId())
                .update();
        if (!rewardResult) {
            log.warn("got_shared_reward_sum failed. memberId({}) amount({})", rebatePlan.getRebateMemberId(), rewardAmount);
            throw new BtBankException(MessageCode.INVALID_ACCOUNT);
        }
    }

    public MinerRebateServiceImpl getService() {
        return SpringContextUtil.getBean(MinerRebateServiceImpl.class);
    }

    /**
     * 奖励释放执行计划
     */
    @Data
    private static class RebatePlan {
        private BigDecimal money;
        private String commissionRateKey;
        private Long refId;
        private String comment;
        private Long txId;
        private Long minerMemberId;
        private Integer type;
        private Long rebateMemberId;
        private Integer rebateLevel;
        private int rebateType;
    }
}
