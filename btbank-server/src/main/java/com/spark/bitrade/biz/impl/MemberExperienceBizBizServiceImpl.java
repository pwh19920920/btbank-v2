package com.spark.bitrade.biz.impl;

import com.spark.bitrade.biz.MemberExperienceBizService;
import com.spark.bitrade.constant.RealNameStatus;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberTransaction;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.entity.SilkDataDist;
import com.spark.bitrade.repository.entity.BtBankMinerBalanceTransaction;
import com.spark.bitrade.repository.entity.MemberExperienceLockRecord;
import com.spark.bitrade.repository.entity.MemberExperienceReleaseRecord;
import com.spark.bitrade.repository.entity.MemberExperienceWallet;
import com.spark.bitrade.repository.service.BtBankMinerBalanceTransactionService;
import com.spark.bitrade.repository.service.MemberExperienceLockRecordService;
import com.spark.bitrade.repository.service.MemberExperienceReleaseRecordService;
import com.spark.bitrade.repository.service.MemberExperienceWalletService;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.service.IMemberTransactionService;
import com.spark.bitrade.service.IMemberWalletService;
import com.spark.bitrade.service.SilkDataDistService;
import com.spark.bitrade.util.BigDecimalUtils;
import com.spark.bitrade.util.DateUtils;
import com.spark.bitrade.util.IdWorkByTwitter;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;

import static com.spark.bitrade.constant.BtBankSystemConfig.*;

@Service
@Slf4j
public class MemberExperienceBizBizServiceImpl implements MemberExperienceBizService {

    @Autowired
    private BtBankConfigService btBankConfigService;
    @Autowired
    private MemberExperienceWalletService memberExperienceWalletService;
    @Autowired
    private MemberExperienceLockRecordService memberExperienceLockRecordService;
    @Autowired
    private IdWorkByTwitter idWorkByTwitter;
    @Autowired
    private SilkDataDistService silkDataDistService;
    @Autowired
    private IMemberTransactionService memberTransactionService;
    @Autowired
    private IMemberWalletService memberWalletService;
    @Autowired
    private BtBankMinerBalanceTransactionService minerBalanceTransactionService;
    @Autowired
    private MemberExperienceReleaseRecordService memberExperienceReleaseRecordService;

    @Value("${sourceMemberId:70653}")
    private Long sourceMemberId;
    public void lockExperienceAmount(Member member,Date limitTime) {

        //查询系统字典
        SilkDataDist silkDataDist = silkDataDistService.findOne("RED_PACK_CONFIG","TOTAL_ACCOUNT_ID");
        if(silkDataDist==null){
            log.error("红包支付账户未配置,请联系管理员");
            return;
        }
        Long totalAccountId = Long.valueOf(silkDataDist.getDictVal());
        BigDecimal transferAmount=minerBalanceTransactionService.sum38AfterTransfer(member.getId(),limitTime);
        transferAmount= Optional.ofNullable(transferAmount).orElse(BigDecimal.ZERO);
        //是否开启新用户注册赠送体验金
        Integer switchStatus = btBankConfigService.getConfig(NEW_MEMBER_EXPERIENCE_SWITCH, (v) -> Integer.parseInt(v.toString()), 1);
        //转入金额限制
        BigDecimal transferAmountMin = btBankConfigService.getConfig(MINIMUM_TRANSFER_AMOUNT, (v) -> new BigDecimal(v.toString()), new BigDecimal(100));
        RealNameStatus realNameStatus = member.getRealNameStatus();
        if (switchStatus==1&& BigDecimalUtils.compare(transferAmount,transferAmountMin)&&realNameStatus==RealNameStatus.VERIFIED){
            if(!memberExperienceLockRecordService.lockRecordExistByMemberId(member.getId())){
                //赠送金额
                BigDecimal giveAmount = btBankConfigService.getConfig(NEW_MEMBER_EXPERIENCE_GIVE_AMOUNT, (v) -> new BigDecimal(v.toString()), new BigDecimal(600));
                //执行锁仓
                log.info("用户:{}赠送体验金开始",member.getId());
                getService().doLock(member,transferAmount,giveAmount,totalAccountId);
                log.info("用户:{}赠送体验金结束",member.getId());
            }
        }
    }

    @Override
    @Async
    public void lockExperience() {
        //查询出3月8号之后注册的用户
        //在此限制时间之后 执行赠送体验金
        Date limitTime = btBankConfigService.getConfig(NEW_MEMBER_EXPERIENCE_REGISTER_TIME, (v) -> DateUtils.parseDatetime(v.toString()), DateUtils.parseDatetime("2020-03-09 00:00:00"));
        List<Member> members = memberExperienceWalletService.findByRegisterTime(limitTime);
        log.info("3月8号之后注册的用户数:{}",members.size());
        for (Member member:members){
            try {
                getService().lockExperienceAmount(member,limitTime);
            }catch (Exception e){
                log.info("用户:{}赠送体验金失败:{}",member.getId(), ExceptionUtils.getFullStackTrace(e));
            }
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void doLock(Member member, BigDecimal transferAmount, BigDecimal giveAmount,Long totalAccountId) {
        Date date = new Date();
        MemberExperienceWallet wallet = memberExperienceWalletService.findByMemberIdAndCoinId(member.getId(), "BT");
        int i = memberExperienceWalletService.increaseLockBalance(wallet.getId(), giveAmount);

        //生成锁仓记录
        MemberExperienceLockRecord lockRecord=new MemberExperienceLockRecord();
        lockRecord.setId(idWorkByTwitter.nextId());
        lockRecord.setMemberId(member.getId());
        lockRecord.setInAmount(transferAmount);
        lockRecord.setLockAmount(giveAmount);
        lockRecord.setReleasedAmount(BigDecimal.ZERO);
        lockRecord.setCoin("BT");
        lockRecord.setRemark("新矿工体验金锁仓");
        lockRecord.setCreateTime(date);
        lockRecord.setUpdateTime(date);
        memberExperienceLockRecordService.save(lockRecord);

        //总账户扣除
        MemberTransaction transaction=new MemberTransaction();
        transaction.setAmount(giveAmount.negate());
        transaction.setCreateTime(date);
        transaction.setMemberId(totalAccountId);
        transaction.setSymbol("BT");
        transaction.setType(TransactionType.NEW_MEMBER_EXPERIENCE_AMOUNT);
        transaction.setFee(BigDecimal.ZERO);
        transaction.setFlag(0);
        transaction.setComment(String.format("用户ID:%s,3月8日后注册实名矿工体验金扣除",member.getId()));

        memberTransactionService.save(transaction);
        MemberTransaction transaction2=new MemberTransaction();
        transaction2.setAmount(giveAmount);
        date.setTime(System.currentTimeMillis()+1000);
        transaction2.setCreateTime(date);
        transaction2.setMemberId(member.getId());
        transaction2.setSymbol("BT");
        transaction2.setType(TransactionType.NEW_MEMBER_EXPERIENCE_AMOUNT);
        transaction2.setFee(BigDecimal.ZERO);
        transaction2.setFlag(0);
        transaction2.setComment("3月8日后注册实名矿工");
        memberTransactionService.save(transaction2);


        MemberTransaction transaction3=new MemberTransaction();
        transaction3.setAmount(giveAmount.negate());
        date.setTime(System.currentTimeMillis()+2000);
        transaction3.setCreateTime(date);
        transaction3.setMemberId(member.getId());
        transaction3.setSymbol("BT");
        transaction3.setType(TransactionType.NEW_MEMBER_EXPERIENCE_AMOUNT_LOCK);
        transaction3.setFee(BigDecimal.ZERO);
        transaction3.setFlag(0);
        transaction3.setComment("新矿工体验金锁仓");
        memberTransactionService.save(transaction3);

        MemberWallet totalWallet = memberWalletService.findByCoinAndMemberId("BT", totalAccountId);
        Assert.notNull(totalWallet,"总账户钱包不存在");
        Boolean trade = memberWalletService.trade(totalWallet.getId(), giveAmount.negate(), BigDecimal.ZERO, BigDecimal.ZERO);
        Assert.isTrue(trade,"总账户钱包余额不足");

    }



    @Override
    @Async
    public void oldMemberRelease() {
        log.info("======================老矿工推荐福利发放开始=====================");

        //奖励金额
        BigDecimal ward = btBankConfigService.getConfig(OLD_MEMBER_INVITER_WARD_AMOUNT, (v) -> new BigDecimal(v.toString()), new BigDecimal(100));

        //推荐佣金奖励账号 钱包
        MemberWallet sendWallet = memberWalletService.findByCoinAndMemberId("BT", sourceMemberId);

        Date limitTime = btBankConfigService.getConfig(NEW_MEMBER_EXPERIENCE_REGISTER_TIME, (v) -> DateUtils.parseDatetime(v.toString()), DateUtils.parseDatetime("2020-03-09 00:00:00"));
        List<Member> members = memberExperienceWalletService.findByRegisterTimeAndInviter(limitTime);
        log.info("满足条件(新用户,自动认证成功,且有邀请人)的新用户个数{}",members.size());

        //查询已经返佣的用户
        List<Long> oldMemberHasReturn = memberExperienceReleaseRecordService.findOldMemberHasReturn();
        log.info("已经返佣的新用户{}",oldMemberHasReturn);

        Map<Long, BigDecimal> longBigDecimalMap = filterTrans(limitTime);
        log.info("满足收益的用户{}",longBigDecimalMap);

        for (Member member:members){
            //是否已经返佣
            if (oldMemberHasReturn.contains(member.getId())){
                log.info("新用户{},已经返佣不在计算",member.getId());
                continue;
            }
            //计算新用户收益是否满足
            BigDecimal profit = longBigDecimalMap.get(member.getId());
            if (profit==null){
                log.info("用户{},累计收益不满足,不在计算",member.getId());
                continue;
            }

            try {
                getService().doOldMemberRelease(member,sourceMemberId,ward,profit,sendWallet.getId());
                log.info("用户{},发放成功",member.getInviterId());
            }catch (Exception e){
                log.info("用户{},释放失败",member.getId());
                log.info("释放异常",ExceptionUtils.getFullStackTrace(e));
            }

        }

        log.info("======================老矿工推荐福利发放结束=====================");
    }


    /**
     * 执行老用户释放
     * @param member
     */
    @Transactional(rollbackFor = Exception.class)
    public void doOldMemberRelease(Member member,Long sendMemberId,BigDecimal releaseAmount,BigDecimal income,Long sendWalletId){
        if (member.getInviterId()==null){
            log.info("用户:{},推荐人ID为null",member.getId());
            return;
        }
        Date date=new Date();
        //创建释放记录
        MemberExperienceReleaseRecord releaseRecord=new MemberExperienceReleaseRecord();
        releaseRecord.setMemberId(member.getId());
        releaseRecord.setReleaseAmount(releaseAmount);
        releaseRecord.setCoin("BT");
        releaseRecord.setRemark(String.format("3月8号后直推自动实名矿工%s挖矿收益满%sBT奖励",member.getId(),releaseAmount.toPlainString()));
        releaseRecord.setCreateTime(date);
        releaseRecord.setUpdateTime(date);
        releaseRecord.setReleaseType(2);
        releaseRecord.setTotalIncome(income);
        releaseRecord.setInviterId(member.getInviterId());

        memberExperienceReleaseRecordService.save(releaseRecord);

        //扣款
        Boolean trade = memberWalletService.trade(sendWalletId, releaseAmount.negate(), BigDecimal.ZERO, BigDecimal.ZERO);
        Assert.isTrue(trade,"推荐佣金账户余额不足");

        MemberTransaction transaction=new MemberTransaction();
        transaction.setAmount(releaseAmount.negate());
        transaction.setCreateTime(date);
        transaction.setMemberId(sendMemberId);
        transaction.setSymbol("BT");
        transaction.setType(TransactionType.DIRECT_MINER_REWARD);
        transaction.setFee(BigDecimal.ZERO);
        transaction.setFlag(0);
        transaction.setComment(String.format("老会员:%s,3月8号后直推自动实名矿工%s挖矿收益满%sBT发放",member.getInviterId(),member.getId(),releaseAmount.toPlainString()));
        memberTransactionService.save(transaction);

        //增加
        MemberWallet inviterWallet = memberWalletService.findByCoinAndMemberId("BT", member.getInviterId());
        Boolean trade1 = memberWalletService.trade(inviterWallet.getId(), releaseAmount, BigDecimal.ZERO, BigDecimal.ZERO);
        Assert.isTrue(trade1,"余额增加失败");

        MemberTransaction transaction2=new MemberTransaction();
        transaction2.setAmount(releaseAmount);
        transaction2.setCreateTime(date);
        transaction2.setMemberId(member.getInviterId());
        transaction2.setSymbol("BT");
        transaction2.setType(TransactionType.DIRECT_MINER_REWARD);
        transaction2.setFee(BigDecimal.ZERO);
        transaction2.setFlag(0);
        transaction2.setComment(String.format("3月8号后直推自动实名矿工%s挖矿收益满%sBT奖励",member.getId(),releaseAmount.toPlainString()));
        memberTransactionService.save(transaction2);


    }

    /**
     * 满足收益的用户
     * @param limitTime
     * @return
     */
    private Map<Long,BigDecimal> filterTrans(Date limitTime){
        Map<Long,BigDecimal> map=new HashMap<>();
        BigDecimal sumProfit = btBankConfigService.getConfig(OLD_MEMBER_INVITER_PROFIT_TOTAL_CONDITION, (v) -> new BigDecimal(v.toString()), new BigDecimal(100));
        List<BtBankMinerBalanceTransaction> transactions = minerBalanceTransactionService.countProfitByType(limitTime);
        Iterator<BtBankMinerBalanceTransaction> iterator = transactions.iterator();
        while (iterator.hasNext()){
            BtBankMinerBalanceTransaction transaction = iterator.next();
            BigDecimal sumMoney = transaction.getSumMoney();
            if (sumMoney!=null&&BigDecimalUtils.compare(sumMoney,sumProfit)){
                map.put(transaction.getMemberId(),sumMoney);
            }
        }
        return map;
    }

    public MemberExperienceBizBizServiceImpl getService(){
        return SpringContextUtil.getBean(MemberExperienceBizBizServiceImpl.class);
    }



}















