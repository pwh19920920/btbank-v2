package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberTransaction;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.repository.entity.MemberExperienceLockRecord;
import com.spark.bitrade.repository.entity.MemberExperienceReleaseRecord;
import com.spark.bitrade.repository.entity.MemberExperienceWallet;
import com.spark.bitrade.repository.mapper.MemberExperienceWalletMapper;
import com.spark.bitrade.repository.service.MemberExperienceLockRecordService;
import com.spark.bitrade.repository.service.MemberExperienceReleaseRecordService;
import com.spark.bitrade.repository.service.MemberExperienceWalletService;
import com.spark.bitrade.service.IMemberApiService;
import com.spark.bitrade.service.IMemberTransactionService;
import com.spark.bitrade.service.IMemberWalletService;
import com.spark.bitrade.util.IdWorkByTwitter;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 新用户3月8之后体验金账户 服务实现类
 * </p>
 *
 * @author qhliao
 * @since 2020-03-06
 */
@Service
@Slf4j
public class MemberExperienceWalletServiceImpl extends ServiceImpl<MemberExperienceWalletMapper, MemberExperienceWallet> implements MemberExperienceWalletService {

    @Autowired
    private IdWorkByTwitter idWorkByTwitter;

    @Autowired
    private MemberExperienceLockRecordService lockRecordService;

    @Autowired
    private IMemberApiService iMemberApiService;

    @Autowired
    private MemberExperienceReleaseRecordService releaseRecordService;

    @Autowired
    private MemberExperienceWalletService memberExperienceWalletService;

    @Autowired
    private IMemberTransactionService memberTransactionService;

    @Autowired
    private IMemberWalletService memberWalletService;

    @Override
    public MemberExperienceWallet findByMemberIdAndCoinId(Long memberId, String coinId) {
        Optional<MemberExperienceWallet> optional = baseMapper.findByMemberIdAndCoinId(memberId, coinId);
        if (optional.isPresent()){
            return optional.get();
        }
        MemberExperienceWallet wallet=new MemberExperienceWallet();
        wallet.setId(idWorkByTwitter.nextId());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setFrozenBalance(BigDecimal.ZERO);
        wallet.setLockBalance(BigDecimal.ZERO);
        wallet.setMemberId(memberId);
        wallet.setVersion(100);
        wallet.setCoinId(coinId);
        wallet.setIsLock(0);
        wallet.setEnabledOut(1);
        wallet.setEnabledIn(1);
        wallet.setCreateTime(new Date());
        wallet.setUpdateTime(new Date());
        baseMapper.insert(wallet);
        return wallet;
    }

    @Override
    public int increaseLockBalance(Long walletId, BigDecimal amount) {
        return baseMapper.increaseLockBalance(walletId,amount);
    }

    @Override
    public int decreaseLockBalance(Long walletId, BigDecimal amount) {
        return baseMapper.decreaseLockBalance(walletId,amount);
    }

    @Override
    public List<Member> findByRegisterTime(Date date) {
        return baseMapper.findByRegisterTime(date);
    }

    @Override
    public List<Member> findByRegisterTimeAndInviter(Date date) {
        return baseMapper.findByRegisterTimeAndInviter(date);
    }

    @Override
    @Transactional
    public Boolean deductMemberExperienceWallet(Long memberId, BigDecimal sumProfit) {
        MessageRespResult<Member> member = iMemberApiService.getMember(memberId);
        MemberExperienceLockRecord byMemberId = lockRecordService.findByMemberId(memberId);
        if (byMemberId != null && byMemberId.getLockAmount().compareTo(byMemberId.getReleasedAmount()) != 0) {
            log.info("开始释放： 会员Id: {}, 累计收益： {}", memberId, sumProfit);
            //添加释放记录
            MemberExperienceReleaseRecord record = new MemberExperienceReleaseRecord();
            record.setMemberId(memberId);
            record.setCoin("BT");
            record.setRemark("3月8号后注册实名矿工挖矿收益满600BT释放");
            record.setReleaseType(1);
            record.setInviterId(member.getData().getInviterId());
            record.setTotalIncome(sumProfit);
            record.setReleaseAmount(byMemberId.getLockAmount());
            releaseRecordService.save(record);
            //扣减体验金账户
            MemberExperienceWallet one = memberExperienceWalletService.findByMemberIdAndCoinId(memberId,"BT");
            int i = memberExperienceWalletService.decreaseLockBalance(one.getId(), byMemberId.getLockAmount());
            Assert.isTrue(i>0,"体验金锁仓余额不足");

            byMemberId.setReleasedAmount(byMemberId.getReleasedAmount().add(byMemberId.getLockAmount()));
            byMemberId.setUpdateTime(new Date());
            lockRecordService.saveOrUpdate(byMemberId);
            //增加用户余额
            MemberWallet memberWallet = memberWalletService.findByCoinAndMemberId("BT", record.getMemberId());
            memberWalletService.trade(memberWallet.getId(),byMemberId.getLockAmount(),BigDecimal.ZERO,BigDecimal.ZERO);


            MemberTransaction transaction = new MemberTransaction();
            transaction.setAmount(record.getReleaseAmount());
            transaction.setCreateTime(new Date());
            transaction.setMemberId(memberId);
            transaction.setSymbol("BT");
            transaction.setType(TransactionType.NEW_MEMBER_RELEASE_AMOUNT_LOCK);
            transaction.setFee(BigDecimal.ZERO);
            transaction.setFlag(0);
            transaction.setComment("3月8号后注册实名矿工挖矿收益满600BT释放");
            memberTransactionService.save(transaction);
            log.info("会员Id: {},释放完成..... ", memberId);
            return true;
        }
        return false;
    }


}
