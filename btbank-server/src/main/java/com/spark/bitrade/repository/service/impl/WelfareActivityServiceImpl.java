package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constant.WelfareDateDef;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.repository.entity.WelfareActivity;
import com.spark.bitrade.repository.entity.WelfareInvolvement;
import com.spark.bitrade.repository.mapper.WelfareActivityMapper;
import com.spark.bitrade.repository.service.WelfareActivityService;
import com.spark.bitrade.repository.service.WelfareIncrQualificationService;
import com.spark.bitrade.repository.service.WelfareInvolvementService;
import com.spark.bitrade.repository.service.WelfareNewQualificationService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 福利包活动(WelfareActivity)表服务实现类
 *
 * @author biu
 * @since 2020-04-08 14:14:43
 */
@Slf4j
@Service("welfareActivityService")
@AllArgsConstructor
public class WelfareActivityServiceImpl extends ServiceImpl<WelfareActivityMapper, WelfareActivity>
        implements WelfareActivityService {

    private final WelfareIncrQualificationService incrQualificationService;
    private final WelfareNewQualificationService newQualificationService;
    private final WelfareInvolvementService involvementService;
    private final MemberWalletService memberWalletService;

    @Override
    public WelfareActivity findTheLatest(Integer type) {
        Date yesterday = DateUtils.getCalendarOfYesterday().getTime();
        LambdaQueryWrapper<WelfareActivity> query = new QueryWrapper<WelfareActivity>().lambda().eq(WelfareActivity::getType, type)
                .gt(WelfareActivity::getCreateTime, yesterday)
                .orderByDesc(WelfareActivity::getCreateTime);

        List<WelfareActivity> list = list(query);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<WelfareActivity> findAllByType(Integer type) {
        LambdaQueryWrapper<WelfareActivity> query = new QueryWrapper<WelfareActivity>().lambda().eq(WelfareActivity::getType, type)
                .orderByDesc(WelfareActivity::getCreateTime);
        return list(query);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer chances(Integer type, Member member) {
        if (0 == type) {
            return newQualificationService.chances(member);
        } else if (1 == type) {
            return incrQualificationService.chances(member);
        }
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<WelfareInvolvement> buy(Integer id, Integer number, Member member) {
        WelfareActivity activity = getById(id);
        // 活动检查
        if (activity == null) {
            log.error("福利包活动不存在 [ id = {}, member_id = {} ]", id, member.getId());
            throw BtBankMsgCode.WELFARE_ACTIVITY_NOT_FOUND.asException();
        }

        Date openningTime = activity.getOpenningTime();
        Date closingTime = activity.getClosingTime();

        Date now = Calendar.getInstance().getTime();

        if (openningTime.compareTo(now) > 0) {
            log.error("福利包活动未开盘 [ id = {}, member_id = {} ]", id, member.getId());
            throw BtBankMsgCode.WELFARE_ACTIVITY_NOT_OPEN.asException();
        }
        if (closingTime.compareTo(now) < 0) {
            log.error("福利包活动已封盘 [ id = {}, member_id = {} ]", id, member.getId());
            throw BtBankMsgCode.WELFARE_ACTIVITY_IS_CLOSED.asException();
        }

        List<WelfareInvolvement> involvementList = new ArrayList<>();
        Integer type = activity.getType();

        // 循环次数
        for (Integer i = 0; i < number; i++) {
            long involvementId = IdWorker.getId();

            // 1、尝试扣除次数是否足够，不够终止
            boolean decrease = false;
            if (0 == type) {
                if (member.getRegistrationTime().compareTo(WelfareDateDef.LIMIT_DATE) < 0) {
                    log.error("新人福利包活动没有购买资格 [ id = {}, member_id = {} ]", id, member.getId());
                    throw BtBankMsgCode.WELFARE_INVOLVEMENT_CAN_NOT_BUY.asException();
                }
                decrease = newQualificationService.decrease(member.getId(), involvementId + "");
            } else {
                Integer incr = incrQualificationService.countTotal(member.getId());
                if (incr == null || incr == 0) {
                    log.error("增值福利包活动没有购买资格 [ id = {}, member_id = {} ]", id, member.getId());
                    throw BtBankMsgCode.WELFARE_INVOLVEMENT_CAN_NOT_BUY.asException();
                }
                decrease = incrQualificationService.decrease(member.getId());
            }
            if (!decrease) {
                log.error("福利包活动购买次数不足 [ id = {}, member_id = {} ]", id, member.getId());
                throw BtBankMsgCode.WELFARE_CHANCE_NOT_ENOUGH.asException();
            }

            // 2、写入购买明细记录
            WelfareInvolvement involvement = new WelfareInvolvement();
            involvement.setId(involvementId);
            involvement.setActId(activity.getId());
            involvement.setActName(activity.getName());
            involvement.setActType(activity.getType());
            involvement.setOpenningTime(openningTime);
            involvement.setClosingTime(closingTime);
            involvement.setReleaseTime(activity.getReleaseTime());
            involvement.setMemberId(member.getId());
            involvement.setInviteId(member.getInviterId() == null ? 0 : member.getInviterId());
            involvement.setUsername(member.getUsername());
            involvement.setAmount(activity.getAmount());
            involvement.setEarningUnreleasedAmount(activity.getAmount().multiply(activity.getEarningRate()));
            involvement.setRecommendStatus(0);
            involvement.setStatus(0);
            involvement.setReleaseStatus(0);
            involvement.setCreateTime(Calendar.getInstance().getTime());
            if (!involvementService.save(involvement)) {
                log.error("福利包活动购买写入失败 [ id = {}, member_id = {} ]", id, member.getId());
                throw BtBankMsgCode.WELFARE_INVOLVEMENT_BUY_FAILED.asException();
            }

            // 3、若是新人福利包
            //  a) 检查是否首次购买，非首次跳过
            //  b) 首次购买，为上级插入一条新人福利包购买资格
            if (activity.getType() == 0) {
                if (!newQualificationService.increase(member.getInviterId(), member.getId())) {
                    log.error("福利包活动购买增加上级购买次数失败 [ id = {}, member_id = {} ]", id, member.getId());
                    throw BtBankMsgCode.WELFARE_INVOLVEMENT_BUY_FAILED.asException();
                }
            }


            involvementList.add(involvement);
        }

        String comment = type == 0 ? "新人福利挖矿" : "增值福利挖矿";
        TransactionType txType = type == 0 ? TransactionType.WELFARE_NEW_PACKET_BUY : TransactionType.WELFARE_INCR_PACKET_BUY;

        Map<Long, WalletChangeRecord> involvementRecordMap = new HashMap<>();
        try {
            for (WelfareInvolvement involvement : involvementList) {
                Long involvementId = involvement.getId();
                // 4、尝试扣除钱包余额，失败则事务回滚
                WalletChangeRecord record = memberWalletService.tryTrade(
                        txType,
                        member.getId(),
                        "BT",
                        "BT",
                        activity.getAmount().negate(),
                        involvementId,
                        comment);
                if (record == null) {
                    log.error("福利包活动购买扣除购买账户余额失败 [ id = {}, member_id = {} ]", id, member.getId());
                    throw BtBankMsgCode.WELFARE_INVOLVEMENT_BUY_FAILED.asException();
                }

                LambdaUpdateWrapper<WelfareInvolvement> update = new LambdaUpdateWrapper<WelfareInvolvement>()
                        .eq(WelfareInvolvement::getId, involvementId)
                        .set(WelfareInvolvement::getRefId, record.getId() + "")
                        .set(WelfareInvolvement::getUpdateTime, Calendar.getInstance().getTime());

                if (!involvementService.update(update)) {
                    log.error("购买福利包处理失败 [ id = {}, record = {} ] 失败", involvementId, record);
                    throw BtBankMsgCode.WELFARE_INVOLVEMENT_BUY_FAILED.asException();
                }
                involvementRecordMap.put(involvementId, record);
            }

        } catch (RuntimeException ex) {
            log.error("购买福利包扣除余额异常 开始回滚账户变动 err = {}", ex.getMessage());
            for (Map.Entry<Long, WalletChangeRecord> entry : involvementRecordMap.entrySet()) {
                Long involvementId = entry.getKey();
                WalletChangeRecord record = entry.getValue();
                boolean b = memberWalletService.rollbackTrade(member.getId(), record.getId());
                log.info("购买福利包异常 回滚账户变动 involvement_id = {}, result = {}, record = {}", involvementId, b, record);
            }
            throw ex;
        }

        // 入账
        for (Map.Entry<Long, WalletChangeRecord> entry : involvementRecordMap.entrySet()) {
            Long involvementId = entry.getKey();
            WalletChangeRecord record = entry.getValue();
            if (!memberWalletService.confirmTrade(member.getId(), record.getId())) {
                log.error("购买福利包 确认账户变动失败 [ id = {}, record = {} ]", involvementId, record);
                boolean b = memberWalletService.rollbackTrade(member.getId(), record.getId());
                log.info("购买福利包 回滚账户变动 result = {}, record = {}", b, record);
                throw BtBankMsgCode.WELFARE_INVOLVEMENT_BUY_FAILED.asException();
            }
        }

        return involvementList;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public WelfareInvolvement refund(Long id, Member member) {
        WelfareInvolvement involvement = involvementService.getById(id);
        if (involvement == null || !involvement.getMemberId().equals(member.getId())) {
            throw BtBankMsgCode.WELFARE_INVOLVEMENT_NOT_FOUND.asException();
        }

        Date closingTime = involvement.getClosingTime();
        Date now = Calendar.getInstance().getTime();

        if (0 == involvement.getActType()) {
            log.error("新人福利包不可撤回 [ id = {}, member_id = {} ]", id, member.getId());
            throw BtBankMsgCode.WELFARE_INVOLVEMENT_NO_REFUND.asException();
        }

        if (closingTime.compareTo(now) < 0) {
            log.error("福利包封盘不可撤回 [ id = {}, member_id = {} ]", id, member.getId());
            throw BtBankMsgCode.WELFARE_INVOLVEMENT_NO_REFUND.asException();
        }

        // 归还购买次数
        if (!incrQualificationService.refund(member.getId())) {
            log.error("福利包撤回失败 [ id = {}, member_id = {} ]", id, member.getId());
            throw BtBankMsgCode.WELFARE_INVOLVEMENT_REFUND_FAILED.asException();
        }

        // 还款
        String comment = involvement.getActType() == 0 ? "新人福利挖矿撤回" : "增值福利挖矿撤回";

        // TransactionType type = TransactionType.WELFARE_NEW_PACKET_REFUND; // 新人福利挖矿不可撤回
        // 尝试加帐
        WalletChangeRecord record = memberWalletService.tryTrade(
                TransactionType.WELFARE_INCR_PACKET_REFUND,
                member.getId(), "BT", "BT",
                involvement.getAmount().abs(),
                involvement.getId(),
                comment);

        if (record == null) {
            log.error("福利包退回处理失败 txId = {}, member_id = {}, amount = {}", involvement.getId(), involvement.getMemberId(), involvement.getAmount().abs());
            throw BtBankMsgCode.WELFARE_INVOLVEMENT_REFUND_FAILED.asException();
        }

        try {
            // 修改状态为1已撤回
            LambdaUpdateWrapper<WelfareInvolvement> update = new LambdaUpdateWrapper<WelfareInvolvement>()
                    .eq(WelfareInvolvement::getId, id)
                    .eq(WelfareInvolvement::getStatus, 0)
                    .set(WelfareInvolvement::getStatus, 1)
                    .set(WelfareInvolvement::getRefundRefId, record.getId() + "")
                    .set(WelfareInvolvement::getUpdateTime, now);
            // 撤回次数
            if (involvementService.update(update)) {
                // 确认账户
                boolean b = memberWalletService.confirmTrade(involvement.getMemberId(), record.getId());
                if (!b) {
                    log.error("确认账户变动失败 record = {}", record);
                    throw BtBankMsgCode.WELFARE_INVOLVEMENT_REFUND_FAILED.asException();
                }
            } else {
                log.error("福利包已被撤回 txId = {}", involvement.getId());
                throw BtBankMsgCode.WELFARE_INVOLVEMENT_REFUND_FAILED.asException();
            }
        } catch (RuntimeException ex) {
            log.error("福利包撤回处理失败 txId = {}, err = {}", involvement.getId(), ex.getMessage());
            boolean b = memberWalletService.rollbackTrade(involvement.getMemberId(), record.getId());
            log.info("回滚账户变动 result = {}, record = {}", b, record);
            throw ex;
        }

        return involvement;
    }
}