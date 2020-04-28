package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.api.dto.MemberRateDto;
import com.spark.bitrade.biz.MemberScoreBizService;
import com.spark.bitrade.biz.WelfareReleaseService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.constant.MinerGrade;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.BtBankMinerBalance;
import com.spark.bitrade.repository.entity.BtBankRebateRecord;
import com.spark.bitrade.repository.entity.WelfareInvolvement;
import com.spark.bitrade.repository.service.BtBankMinerBalanceService;
import com.spark.bitrade.repository.service.BtBankRebateRecordService;
import com.spark.bitrade.repository.service.WelfareInvolvementService;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.service.MemberAccountService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.BigDecimalUtils;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * WelfareReleaseServiceImpl
 *
 * @author Archx[archx@foxmail.com]
 * @since 2020/4/13 11:41
 */
@Slf4j
@Service
public class WelfareReleaseServiceImpl implements WelfareReleaseService {

    private final MemberWalletService memberWalletService;
    private final WelfareInvolvementService involvementService;
    private final BtBankMinerBalanceService minerBalanceService;
    private final MemberAccountService memberAccountService;
    private final BtBankConfigService configService;
    private final MemberScoreBizService memberScoreBizService;
    private final BtBankRebateRecordService rebateRecordService;

    public WelfareReleaseServiceImpl(MemberWalletService memberWalletService,
                                     WelfareInvolvementService involvementService,
                                     BtBankMinerBalanceService minerBalanceService,
                                     MemberAccountService memberAccountService,
                                     BtBankConfigService configService,
                                     MemberScoreBizService memberScoreBizService,
                                     BtBankRebateRecordService rebateRecordService) {
        this.memberWalletService = memberWalletService;
        this.involvementService = involvementService;
        this.minerBalanceService = minerBalanceService;
        this.memberAccountService = memberAccountService;
        this.configService = configService;
        this.memberScoreBizService = memberScoreBizService;
        this.rebateRecordService = rebateRecordService;
    }

    @Value("${sourceMemberId:70653}") // 挖矿佣金奖励账户
    private Long sourceMemberId;
    @Value("${btbank.reward.member:70653}") // 推荐佣金奖励账户
    private Long rewardMemberId;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void principal(WelfareInvolvement involvement) {
        Date now = Calendar.getInstance().getTime();

        boolean update = involvementService.lambdaUpdate()
                .eq(WelfareInvolvement::getId, involvement.getId())
                .eq(WelfareInvolvement::getReleaseStatus, 0)
                .set(WelfareInvolvement::getReleaseStatus, 1)
                .set(WelfareInvolvement::getUpdateTime, now).update();
        // 更新挖矿收益释放 为1
        if (!update) {
            log.warn("福利挖矿本金已释放 involvement_id = {}, member_id = {}", involvement.getId(), involvement.getMemberId());
            return;
        }
        // 增加本金
        TransactionType type = involvement.getActType() == 0
                ? TransactionType.WELFARE_NEW_PACKET_BUY : TransactionType.WELFARE_INCR_PACKET_BUY;
        WalletChangeRecord principal = memberWalletService.tryTrade(type,
                involvement.getMemberId(), "BT", "BT",
                involvement.getAmount().abs(),
                involvement.getId(), type.getCnName());

        if (principal == null) {
            throw new BtBankException("福利挖矿增加本金失败");
        }
        try {
            // 确认账户
            boolean b = memberWalletService.confirmTrade(involvement.getMemberId(), principal.getId());
            if (!b) {
                log.error("确认账户变动失败 record = {}", principal);
                throw new BtBankException(CommonMsgCode.FAILURE);
            }
            log.info("福利挖矿本金释放完成 [ involvement_id = {}, member_id = {} ]", involvement.getId(), involvement.getMemberId());
        } catch (RuntimeException ex) {
            log.error("福利挖矿本金发放处理失败 txId = {}, err = {}", involvement.getId(), ex.getMessage());
            boolean b = memberWalletService.rollbackTrade(involvement.getMemberId(), principal.getId());
            log.info("回滚账户变动 result = {}, record = {}", b, principal);
            throw ex;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void interest(WelfareInvolvement involvement) {
        Date now = Calendar.getInstance().getTime();
        // 释放利息
        TransactionType type = involvement.getActType() == 0
                ? TransactionType.WELFARE_NEW_PACKET_INTEREST : TransactionType.WELFARE_INCR_PACKET_INTEREST;


        boolean update = involvementService.lambdaUpdate()
                .eq(WelfareInvolvement::getId, involvement.getId())
                .eq(WelfareInvolvement::getReleaseStatus, 1)
                .set(WelfareInvolvement::getReleaseStatus, 2)
                .set(WelfareInvolvement::getUpdateTime, now).update();
        // 更新挖矿收益释放 为2
        if (!update) {
            log.warn("福利挖矿利息已释放 involvement_id = {}, member_id = {}", involvement.getId(), involvement.getMemberId());
            return;
        }

        String comment = involvement.getActType() == 0 ? "参与新人福利挖矿利息" : "参与增值福利挖矿利息";
        // 从发放账户扣除 sourceMemberId
        WalletChangeRecord decrease = memberWalletService.tryTrade(type,
                sourceMemberId, "BT", "BT",
                involvement.getEarningUnreleasedAmount().negate(),
                involvement.getId(), involvement.getMemberId() + comment);

        if (decrease == null) {
            throw new BtBankException("福利挖矿利息释放 挖矿佣金账户扣款失败");
        }

        // 增加利息
        WalletChangeRecord increase = memberWalletService.tryTrade(type,
                involvement.getMemberId(), "BT", "BT",
                involvement.getEarningUnreleasedAmount().abs(),
                involvement.getId(), type.getCnName());

        if (increase == null) {
            boolean b = memberWalletService.rollbackTrade(sourceMemberId, decrease.getId());
            log.error("福利挖矿利息释放增加余额失败 回滚扣款记录 [ wallet_change_record = {}, result = {} ]", decrease.getId(), b);
            throw new BtBankException("福利挖矿利息释放 增加余额失败");
        }

        // 更新记录
        now = Calendar.getInstance().getTime();

        try {
            update = involvementService.lambdaUpdate()
                    .eq(WelfareInvolvement::getId, involvement.getId())
                    .eq(WelfareInvolvement::getReleaseStatus, 2)
                    .set(WelfareInvolvement::getEarningRefId, decrease.getId() + "," + increase.getRefId())
                    .set(WelfareInvolvement::getEarningReleaseAmount, involvement.getEarningUnreleasedAmount())
                    .set(WelfareInvolvement::getEarningReleaseTime, now)
                    .set(WelfareInvolvement::getUpdateTime, now).update();
            if (update) {
                if (!memberWalletService.confirmTrade(sourceMemberId, decrease.getId())
                        || !memberWalletService.confirmTrade(involvement.getMemberId(), increase.getId())) {
                    throw new BtBankException("福利挖矿利息释放 账户变动入账失败");
                }
                log.info("福利挖矿利息释放完成 [ involvement_id = {}, member_id = {} ]", involvement.getId(), involvement.getMemberId());
            } else {
                throw new BtBankException("福利挖矿利息释放 写入数据记录失败");
            }
        } catch (RuntimeException ex) {
            log.error("福利挖矿利息释放处理失败 txId = {}, err = {}", involvement.getId(), ex.getMessage());
            boolean b = memberWalletService.rollbackTrade(sourceMemberId, decrease.getId());
            log.info("福利挖矿利息释放 回滚账户变动 1 result = {}, record = {}", b, decrease);
            boolean b1 = memberWalletService.rollbackTrade(involvement.getMemberId(), increase.getId());
            log.info("福利挖矿利息释放 回滚账户变动 2 result = {}, record = {}", b1, increase);
            throw ex;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void invite(WelfareInvolvement involvement) {
        // 直推处理
        Date now = Calendar.getInstance().getTime();

        boolean update = involvementService.lambdaUpdate()
                .eq(WelfareInvolvement::getId, involvement.getId())
                .eq(WelfareInvolvement::getReleaseStatus, 2)
                .set(WelfareInvolvement::getReleaseStatus, 3)
                .set(WelfareInvolvement::getUpdateTime, now).update();
        // 更新挖矿收益释放 为3
        if (!update) {
            log.warn("福利挖矿直推已释放 involvement_id = {}, member_id = {}", involvement.getId(), involvement.getMemberId());
            return;
        }

        Member member = memberAccountService.findMemberByMemberId(involvement.getMemberId());
        Long inviterId = 0L;
        if (member.getInviterId() != null) {
            inviterId = member.getInviterId();
        }
        BtBankMinerBalance minerBalance = minerBalanceService.findFirstByMemberId(inviterId);
        if (Objects.isNull(minerBalance) || minerBalance.getMinerGrade() < MinerGrade.SILVER_MINER.getGradeId()) {
            // 仅当推荐人具有矿工身份时，才释放直推奖励给邀请者
            log.info("福利挖矿本金释放完成 [ involvement_id = {}, member_id = {} ]，上级不存在或不是矿工 inviter_id = {}",
                    involvement.getId(), involvement.getMemberId(), inviterId);
            return;
        }

        String commissionRateStr = (String) configService.getConfig(BtBankSystemConfig.SILVER_MINER_COMMISSION_RATE);
        if (Objects.isNull(commissionRateStr)) {
            log.warn("query MINER_COMMISSION_RATE config failed. Key:{}", BtBankSystemConfig.SILVER_MINER_COMMISSION_RATE);
            throw new BtBankException(BtBankMsgCode.FAILED_TO_LOAD_SYSTEM_CONFIGURATION);
        }

        BigDecimal commissionRate = new BigDecimal(commissionRateStr);
        //4月1日 0:00后推荐注册的有效矿工，可获得其每笔挖矿收益：前30天100%，第二个30天50%，然后恢复10%的奖励（大宗挖矿的直推奖励也按这个比例）
        MemberRateDto dto = memberScoreBizService.aprilOneRate(member.getRegistrationTime());
        if (dto.getRate() != null) {
            commissionRate = dto.getRate();
        }
        BigDecimal rewardAmount = BigDecimalUtils.mulDown(involvement.getEarningUnreleasedAmount(), commissionRate, 2);

        if (BigDecimalUtil.lte0(rewardAmount)) {
            // 这里的 rewardAmount 在两位小数的情况下，可能直接等于 0，后面的逻辑没有意义
            log.info("福利挖矿直推释放完成 [ involvement_id = {}, member_id = {} ], 奖励金额不足 < 0", involvement.getId(), involvement.getMemberId());
            return;
        }
        boolean b = memberScoreBizService.addPendingRecord(inviterId, rewardAmount,
                member.getId(), involvement.getActType() == 0 ? 2 : 3, involvement.getId(), involvement.getActType() == 0 ?
                        inviterId + "推荐矿工" + member.getId() + "购买新人福利包" : inviterId + "推荐" + member.getId() + "参与增值福利挖矿");

        if (!b) {
            throw new BtBankException("添加直推待领取记录失败");
        } else {
            now = Calendar.getInstance().getTime();
            update = involvementService.lambdaUpdate()
                    .eq(WelfareInvolvement::getId, involvement.getId())
                    .eq(WelfareInvolvement::getReleaseStatus, 3)
                    .set(WelfareInvolvement::getRecommendStatus, 1)
                    // .set(WelfareInvolvement::getRecommendRefId, ) 领取时设置
                    .set(WelfareInvolvement::getRecommendAmount, rewardAmount)
                    .set(WelfareInvolvement::getRecommendReleaseTime, now)
                    .set(WelfareInvolvement::getUpdateTime, now).update();
            // 更新挖矿收益释放 为3
            if (!update) {
                log.warn("福利挖矿直推释放 更新记录失败 involvement_id = {}, member_id = {}", involvement.getId(), involvement.getMemberId());
                throw new BtBankException("更新直推释放记录失败");
            }
            log.info("福利挖矿直推释放完成 [ involvement_id = {}, member_id = {} ]", involvement.getId(), involvement.getMemberId());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void gold(WelfareInvolvement involvement) {
        // 金牌处理
        Date now = Calendar.getInstance().getTime();

        boolean update = involvementService.lambdaUpdate()
                .eq(WelfareInvolvement::getId, involvement.getId())
                .eq(WelfareInvolvement::getReleaseStatus, 3)
                .set(WelfareInvolvement::getReleaseStatus, 4)
                .set(WelfareInvolvement::getUpdateTime, now).update();
        // 更新挖矿收益释放 为4
        if (!update) {
            log.warn("福利挖矿金牌已释放 involvement_id = {}, member_id = {}", involvement.getId(), involvement.getMemberId());
            return;
        }

        Member member = memberAccountService.findMemberByMemberId(involvement.getMemberId());
        Long inviterId = 0L;
        if (member.getInviterId() != null) {
            inviterId = member.getInviterId();
        }
        // 查询祖父级推荐人
        Member fatherMember = memberAccountService.findMemberByMemberId(inviterId);
        if (Objects.isNull(fatherMember) || Objects.isNull(fatherMember.getInviterId())) {
            log.info("福利挖矿金牌释放完成 [ involvement_id = {}, member_id = {} ]，父级或祖父级不存在 inviterId = {}",
                    involvement.getId(), involvement.getMemberId(), inviterId);
            return;
        }
        doReleaseGold(member, involvement, fatherMember.getInviterId(), 2);
    }

    @Transactional(rollbackFor = Exception.class)
    public void doReleaseGold(Member member, WelfareInvolvement involvement, Long inviterId, Integer level) {
        // 递归查询释放金牌矿工奖励
        BtBankMinerBalance minerBalance = minerBalanceService.findFirstByMemberId(inviterId);
        if (Objects.nonNull(minerBalance) && minerBalance.getMinerGrade() == MinerGrade.GOLD_MINER.getGradeId()) {
            // 金牌矿工身份，释放奖励
            // MEMO: 2019-11-18 需求变更：金牌矿工返佣，只返回给上级第一个具有金牌矿工身份的人

            // 获取金牌奖励比例
            String commissionRateStr = (String) configService.getConfig(BtBankSystemConfig.GOLD_MINER_COMMISSION_RATE);
            if (Objects.isNull(commissionRateStr)) {
                log.warn("query MINER_COMMISSION_RATE config failed. Key:{}", BtBankSystemConfig.GOLD_MINER_COMMISSION_RATE);
                throw new BtBankException(BtBankMsgCode.FAILED_TO_LOAD_SYSTEM_CONFIGURATION);
            }

            BigDecimal commissionRate = new BigDecimal(commissionRateStr);
            BigDecimal rewardAmount = BigDecimalUtils.mulDown(involvement.getEarningUnreleasedAmount(), commissionRate, 2);

            if (BigDecimalUtil.lte0(rewardAmount)) {
                // 这里的 rewardAmount 在两位小数的情况下，可能直接等于 0，后面的逻辑没有意义
                log.info("福利挖矿金牌释放完成 [ involvement_id = {}, member_id = {} ], 奖励金额不足 < 0", involvement.getId(), involvement.getMemberId());
                return;
            }
            doReleaseGold(member, involvement, rewardAmount, inviterId, level);
            log.info("福利挖矿金牌释放完成 [ involvement_id = {}, member_id = {} ]", involvement.getId(), involvement.getMemberId());
            return;
        }

        // 递归查询金牌矿工身份释放金牌矿工奖励
        Member inviter = memberAccountService.findMemberByMemberId(inviterId);
        if (Objects.nonNull(member) && Objects.nonNull(inviter.getInviterId())) {
            getService().doReleaseGold(member, involvement, inviter.getInviterId(), level + 1);
        } else {
            log.info("福利挖矿金牌释放完成 [ involvement_id = {}, member_id = {} ], 推荐关系断裂 inviter_id = {}",
                    involvement.getId(), involvement.getMemberId(), inviterId);
        }
    }

    private void doReleaseGold(Member member, WelfareInvolvement involvement, BigDecimal amount, Long inviterId, Integer level) {

        // 从发放账户扣除 rewardMemberId
        WalletChangeRecord decrease = memberWalletService.tryTrade(TransactionType.GOLDEN_MINER_REWARD,
                rewardMemberId, "BT", "BT",
                amount.negate(),
                involvement.getId(), String.format("%d参与%s福利挖矿金牌佣金奖励", member.getId(), involvement.getActType() == 0 ? "新人" : "增值"));

        if (decrease == null) {
            throw new BtBankException("福利挖矿金牌释放 推荐佣金账户扣款失败");
        }

        // 增加金牌
        WalletChangeRecord increase = memberWalletService.tryTrade(TransactionType.GOLDEN_MINER_REWARD,
                inviterId, "BT", "BT",
                amount.abs(),
                involvement.getId(), String.format("%s福利挖矿金牌佣金奖励", involvement.getActType() == 0 ? "新人" : "增值"));

        if (increase == null) {
            boolean b = memberWalletService.rollbackTrade(rewardMemberId, decrease.getId());
            log.error("福利挖矿金牌释放 增加余额失败 回滚扣款记录 [ wallet_change_record = {}, result = {} ]", decrease.getId(), b);
            throw new BtBankException("福利挖矿金牌释放 增加余额失败");
        }

        // 保存奖励记录
        saveRebateRecord(involvement, inviterId, amount, level, increase.getId());

        // 更新记录
        Date now = Calendar.getInstance().getTime();

        try {
            boolean update = involvementService.lambdaUpdate()
                    .eq(WelfareInvolvement::getId, involvement.getId())
                    .eq(WelfareInvolvement::getReleaseStatus, 4)
                    .set(WelfareInvolvement::getGoldRefId, decrease.getId() + "," + increase.getRefId())
                    .set(WelfareInvolvement::getUpdateTime, now).update();
            if (update) {
                if (!memberWalletService.confirmTrade(rewardMemberId, decrease.getId())
                        || !memberWalletService.confirmTrade(inviterId, increase.getId())) {
                    throw new BtBankException("福利挖矿金牌释放 账户变动入账失败");
                }
            } else {
                throw new BtBankException("福利挖矿金牌释放 写入数据记录失败");
            }
        } catch (RuntimeException ex) {
            log.error("福利挖矿金牌释放处理失败 txId = {}, err = {}", involvement.getId(), ex.getMessage());
            boolean b = memberWalletService.rollbackTrade(rewardMemberId, decrease.getId());
            log.info("福利挖矿金牌释放 回滚账户变动 1 result = {}, record = {}", b, decrease);
            boolean b1 = memberWalletService.rollbackTrade(involvement.getMemberId(), increase.getId());
            log.info("福利挖矿金牌释放 回滚账户变动 2 result = {}, record = {}", b1, increase);
            throw ex;
        }
    }

    private void saveRebateRecord(WelfareInvolvement involvement,
                                  Long inviterId,
                                  BigDecimal rewardAmount,
                                  Integer level,
                                  Long refId) {
        // 保存奖励流水
        BtBankRebateRecord rebateRecord = new BtBankRebateRecord();
        rebateRecord.setId(IdWorker.getId());
        rebateRecord.setMinerBalanceTransactionId(involvement.getId());
        rebateRecord.setMinerMemberId(involvement.getMemberId());
        rebateRecord.setMinerRewardType(involvement.getActType() == 0 ? 14 : 15); // 14新人福利挖矿 15增值福利挖矿
        rebateRecord.setRewardAmount(involvement.getEarningUnreleasedAmount());
        rebateRecord.setRebateAmount(rewardAmount);
        rebateRecord.setRebateMemberId(inviterId);
        rebateRecord.setRebateLevel(level);
        rebateRecord.setRebateType(1);
        rebateRecord.setRefId(refId);

        Date now = new Date();
        rebateRecord.setCreateTime(now);
        rebateRecord.setUpdateTime(now);
        if (!rebateRecordService.save(rebateRecord)) {
            throw new BtBankException(BtBankMsgCode.RELEASE_REWARD_FAILED);
        }
    }

    private WelfareReleaseServiceImpl getService() {
        return SpringContextUtil.getBean(this.getClass());
    }
}
