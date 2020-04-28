package com.spark.bitrade.biz.impl;

import com.spark.bitrade.biz.PlanAssetService;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.enums.MessageCode;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.RedPackExperienceGold;
import com.spark.bitrade.repository.entity.RedPackLock;
import com.spark.bitrade.repository.service.RedPackExperienceGoldService;
import com.spark.bitrade.repository.service.RedPackLockService;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author shenzucai
 * @time 2019.12.08 13:52
 */
@Service
@Slf4j
public class PlanAssetServiceImpl implements PlanAssetService {

    @Autowired
    private RedPackExperienceGoldService redPackExperienceGoldService;

    @Autowired
    private RedPackLockService redPackLockService;

    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private BtBankConfigService btBankConfigService;

    /**
     * type活动类型member用户解锁体验金amount数量
     *
     * @param member
     * @param amount
     * @param type   (1,参与挖矿，2推荐矿工，3挖矿收益)
     * @return true
     * @author shenzucai
     * @time 2019.12.08 13:50
     */
    @Override
    @Async
    public Boolean doUnlock(Member member, BigDecimal amount, Integer type, Long minePoolId, Long miningId) {
        if (Objects.isNull(member)) {
            return Boolean.FALSE;
        }
        switch (type) {
            case 1:
                SpringContextUtil.getBean(PlanAssetServiceImpl.class).mineUnlock(member, amount, miningId);
                break;
            case 2:
                SpringContextUtil.getBean(PlanAssetServiceImpl.class).inviteUnlock(member, amount);
                break;
            case 3:
                SpringContextUtil.getBean(PlanAssetServiceImpl.class).rewardUnlock(member, amount, minePoolId);
                break;
            default:
                break;
        }
        return Boolean.TRUE;
    }

    /**
     * 1,参与挖矿
     *
     * @param member
     * @param amount
     * @return true
     * @author shenzucai
     * @time 2019.12.08 13:58
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean mineUnlock(Member member, BigDecimal amount, Long miningId) {
        // 1,判断是否已有该类型的挖矿
        List<RedPackExperienceGold> redPackExperienceGolds = redPackExperienceGoldService.lambdaQuery()
                .eq(RedPackExperienceGold::getMemberId, member.getId())
                .eq(RedPackExperienceGold::getActivityType, 2)
                .eq(RedPackExperienceGold::getLockType, 2).list();
        // 2，如果没有则进行解锁操作
        if (redPackExperienceGolds != null && redPackExperienceGolds.size() > 0) {
            return Boolean.TRUE;
        }

        // 2.1 扣除红包锁仓数量
        Boolean deal = redPackLockService.lambdaUpdate()
                .setSql("lock_amount = lock_amount - " + amount).eq(RedPackLock::getMemberId, member.getId())
                .ge(RedPackLock::getLockAmount, amount).update();
        if (!deal) {
            log.info("挖矿解锁红包失败 用户{},金额{},挖矿id {}", member, amount, miningId);
            return Boolean.FALSE;
        }
        // 2.2 产生流水
        RedPackExperienceGold redPackExperienceGold = new RedPackExperienceGold();
        redPackExperienceGold.setMemberId(member.getId());
        redPackExperienceGold.setActivityType(2);
        redPackExperienceGold.setLockType(2);
        redPackExperienceGold.setAmount(amount);
        redPackExperienceGold.setCreateTime(new Date());
        redPackExperienceGold.setUpdateTime(new Date());
        redPackExperienceGold.setMiningId(miningId);

        Boolean saveRed = redPackExperienceGoldService.saveGetId(redPackExperienceGold);
        if (!saveRed) {
            log.info("挖矿解锁红包记录保存失败 用户{},金额{},挖矿id {}", member, amount, miningId);
            throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }
        MessageRespResult respResult = memberWalletService.optionMemberWalletBalance(TransactionType.ACTIVITY_AWARD, member.getId()
                , "BT", "BT", amount, redPackExperienceGold.getId(), "红包体验金活动");

        Boolean succeeded = (Boolean) respResult.getData();
        if (!succeeded) {
            log.info("挖矿解锁红包到可用余额失败. memberId({}) amount({})", member.getId(), amount);
            throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }

        return Boolean.TRUE;
    }

    /**
     * 2推荐矿工
     *
     * @param member
     * @param amount
     * @return true
     * @author shenzucai
     * @time 2019.12.08 13:58
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean inviteUnlock(Member member, BigDecimal amount) {
        // 0,判断该用户是否已经有过记录
        RedPackExperienceGold aRedPackExperienceGold = redPackExperienceGoldService.lambdaQuery().eq(RedPackExperienceGold::getMemberId, member.getInviterId())
                .eq(RedPackExperienceGold::getActivityType, 2)
                .eq(RedPackExperienceGold::getLockType, 3)
                .eq(RedPackExperienceGold::getChildMemberId, member.getId()).one();

        if (!Objects.isNull(aRedPackExperienceGold)) {
            return Boolean.TRUE;
        }
        // 1,判断是否已有该类型的推荐
        List<RedPackExperienceGold> redPackExperienceGolds = redPackExperienceGoldService.lambdaQuery()
                .eq(RedPackExperienceGold::getMemberId, member.getInviterId())
                .eq(RedPackExperienceGold::getActivityType, 2)
                .eq(RedPackExperienceGold::getLockType, 3).list();
        // 2，如果没有则进行解锁操作
        if (redPackExperienceGolds != null && redPackExperienceGolds.size() > 8) {
            return Boolean.TRUE;
        }

        // 2.1 扣除红包锁仓数量
        Boolean deal = redPackLockService.lambdaUpdate()
                .setSql("lock_amount = lock_amount - " + amount).eq(RedPackLock::getMemberId, member.getInviterId())
                .ge(RedPackLock::getLockAmount, amount).update();
        if (!deal) {
            log.info("邀请解锁红包失败 用户{},金额{}", member, amount);
            return Boolean.FALSE;
        }
        // 2.2 产生流水
        RedPackExperienceGold redPackExperienceGold = new RedPackExperienceGold();
        redPackExperienceGold.setMemberId(member.getInviterId());
        redPackExperienceGold.setActivityType(2);
        redPackExperienceGold.setLockType(3);
        redPackExperienceGold.setAmount(amount);
        redPackExperienceGold.setCreateTime(new Date());
        redPackExperienceGold.setUpdateTime(new Date());
        redPackExperienceGold.setChildMemberId(member.getId());

        Boolean saveRed = redPackExperienceGoldService.saveGetId(redPackExperienceGold);
        if (!saveRed) {
            log.info("邀请解锁红包记录保存失败 用户{},金额{}", member, amount);
            throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }
        MessageRespResult respResult = memberWalletService.optionMemberWalletBalance(TransactionType.ACTIVITY_AWARD, member.getInviterId()
                , "BT", "BT", amount, redPackExperienceGold.getId(), "红包体验金活动");

        Boolean succeeded = (Boolean) respResult.getData();
        if (!succeeded) {
            log.info("邀请解锁红包到可用余额失败. memberId({}) amount({})", member.getId(), amount);
            throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }

        return Boolean.TRUE;
    }

    /**
     * 3挖矿收益
     *
     * @param member
     * @param amount
     * @return true
     * @author shenzucai
     * @time 2019.12.08 13:58
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean rewardUnlock(Member member, BigDecimal amount, Long minePoolId) {
        if (!btBankConfigService.isNewMemberConfig(member)) {
            log.info("----------->>> 不是新用戶,不進入600BT解锁规则 ------------memberId={}", member.getId());
            return false;
        }

        // 1,判断是否已有该类型的推荐
        List<RedPackExperienceGold> redPackExperienceGolds = redPackExperienceGoldService.lambdaQuery()
                .eq(RedPackExperienceGold::getMemberId, member.getId())
                .eq(RedPackExperienceGold::getActivityType, 2)
                .eq(RedPackExperienceGold::getLockType, 4).list();

        // 2，如果没有则进行解锁操作
        BigDecimal sumAmount = BigDecimal.ZERO;
        if (redPackExperienceGolds != null && redPackExperienceGolds.size() > 0) {
            for (RedPackExperienceGold redPackExperienceGold : redPackExperienceGolds) {
                sumAmount = sumAmount.add(redPackExperienceGold.getAmount());
            }
        }
        if (sumAmount.add(amount).compareTo(new BigDecimal("600")) == 1) {
            amount = new BigDecimal("600").subtract(sumAmount);
            if (amount.compareTo(BigDecimal.ZERO) != 1) {
                return Boolean.FALSE;
            }
        }
        // 2.1 扣除红包锁仓数量
        Boolean deal = redPackLockService.lambdaUpdate()
                .setSql("lock_amount = lock_amount - " + amount).eq(RedPackLock::getMemberId, member.getId())
                .ge(RedPackLock::getLockAmount, amount).update();
        if (!deal) {
            log.info("收益解锁红包失败 用户{},金额{},收益id {}", member, amount, minePoolId);
            throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }
        // 2.2 产生流水
        RedPackExperienceGold redPackExperienceGold = new RedPackExperienceGold();
        redPackExperienceGold.setMemberId(member.getId());
        redPackExperienceGold.setActivityType(2);
        redPackExperienceGold.setLockType(4);
        redPackExperienceGold.setAmount(amount);
        redPackExperienceGold.setCreateTime(new Date());
        redPackExperienceGold.setUpdateTime(new Date());
        redPackExperienceGold.setMinePoolId(minePoolId);

        Boolean saveRed = redPackExperienceGoldService.saveGetId(redPackExperienceGold);
        if (!saveRed) {
            log.info("收益解锁红包记录保存失败 用户{},金额{}", member, amount);
            throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }
        MessageRespResult respResult = memberWalletService.optionMemberWalletBalance(TransactionType.ACTIVITY_AWARD, member.getId()
                , "BT", "BT", amount, redPackExperienceGold.getId(), "红包体验金活动");

        Boolean succeeded = (Boolean) respResult.getData();
        if (!succeeded) {
            log.info("收益解锁红包到可用余额失败. memberId({}) amount({})", member.getId(), amount);
            throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }
        return Boolean.TRUE;
    }
}
