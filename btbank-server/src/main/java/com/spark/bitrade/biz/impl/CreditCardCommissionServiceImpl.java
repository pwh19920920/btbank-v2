package com.spark.bitrade.biz.impl;

import com.spark.bitrade.api.vo.CreditCardCommissionRefundVO;
import com.spark.bitrade.biz.CreditCardCommissionService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.CreditCardCommissionRecord;
import com.spark.bitrade.repository.service.CreditCardCommissionRecordService;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.service.IMemberTransactionService;
import com.spark.bitrade.service.IMemberWalletService;
import com.spark.bitrade.service.MemberAccountService;
import com.spark.bitrade.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class CreditCardCommissionServiceImpl implements CreditCardCommissionService {

    @Autowired
    private MemberAccountService MemberAccountService;

    @Autowired
    private CreditCardCommissionRecordService  creditCardCommissionRecordService;

    @Value("${btbank.reward.member:70653}")
    private Long rewardMemberId;
    @Value("${dmz.cipher:*20191021Pay#}")
    private String cipher;
    @Autowired
    private IMemberWalletService memberWalletService;
    @Autowired
    private IMemberTransactionService memberTransactionService;
    @Autowired
    private BtBankConfigService configService;
    /**
     * 信用卡手续费保存
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(CreditCardCommissionRefundVO vo) {
        CreditCardCommissionRecord one = creditCardCommissionRecordService.lambdaQuery().eq(CreditCardCommissionRecord::getHashCode, vo.getHashCode()).one();
        if (one!=null){
            throw new BtBankException(BtBankMsgCode.DUPLICATE_DATA);
        }
        //签名验证
        this.authenticSignature(vo);
        Date now=new Date();
        Member member = MemberAccountService.findMemberByPhone(vo.getMobilePhone());
        //保存信用卡手续费记录
        CreditCardCommissionRecord record=new CreditCardCommissionRecord();
        record.setMemberId(member.getId());
        record.setCommissionAmount(vo.getCommissionAmount());
        record.setStatus(0);
        record.setRemark("未解锁");
        record.setUnLockAmount(BigDecimal.ZERO);
        record.setHashCode(vo.getHashCode());
        record.setRefId(vo.getRefId());
        boolean save = creditCardCommissionRecordService.save(record);
        AssertUtil.isTrue(save, CommonMsgCode.UNKNOWN_ERROR);

        //总账户扣款
        MemberWallet reWallet = memberWalletService.findByCoinAndMemberId("BT", rewardMemberId);
        memberWalletService.trade(reWallet.getId(),vo.getCommissionAmount().negate(), BigDecimal.ZERO,BigDecimal.ZERO);
        //资金流水
        memberTransactionService.createTransaction(vo.getCommissionAmount().negate(),rewardMemberId, TransactionType.CREDIT_CARD_COMMISSION,
                String.format("%s代还手续费返还",member.getId()),now);

        //增加锁仓金额
        MemberWallet memberWallet = memberWalletService.findByCoinAndMemberId("BT", member.getId());
        memberWalletService.trade(memberWallet.getId(),BigDecimal.ZERO,BigDecimal.ZERO,vo.getCommissionAmount());
        //资金流水
        memberTransactionService.createTransaction(vo.getCommissionAmount(),member.getId(),TransactionType.CREDIT_CARD_COMMISSION,
                "代还手续费返还",now);
        now.setTime(System.currentTimeMillis()+1000);
        memberTransactionService.createTransaction(vo.getCommissionAmount().negate(),member.getId(),TransactionType.CREDIT_CARD_COMMISSION_LOCK,
                "代还手续费返还锁仓",now);

    }


    /**
     * 解锁信用卡手续费
     * @param amount
     * @param memberId
     */
    @Override
    public void unLockRefund(BigDecimal amount,Long memberId){
        BigDecimal rate = configService.getConfig(BtBankSystemConfig.CREDIT_CARD_COMMISSION_RELEASE_RATE, v -> new BigDecimal(v.toString()), new BigDecimal(1));
        //查询出 手续费记录
        List<CreditCardCommissionRecord> list=creditCardCommissionRecordService.queryUnLockList(memberId);
        BigDecimal temp=amount.multiply(rate);
        for (CreditCardCommissionRecord record:list){
                try {
                    BigDecimal commissionAmount = record.getCommissionAmount();
                    BigDecimal unLockAmount = record.getUnLockAmount();
                    BigDecimal pendingUnLock=commissionAmount.subtract(unLockAmount);
                    if (!BigDecimalUtil.gt0(temp)){
                        break;
                    }
                    if(BigDecimalUtils.compare(temp,pendingUnLock)){
                        getService().unLock(record, pendingUnLock);
                        temp=temp.subtract(pendingUnLock);
                    }else {
                        getService().unLock(record, temp);
                        break;
                    }
                }catch (Exception e){
                    log.info("解锁失败:{}",record.getId());
                    log.error(ExceptionUtils.getFullStackTrace(e));
                }
        }

    }

    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public void unLock(CreditCardCommissionRecord record,BigDecimal amount){
        BigDecimal add = record.getUnLockAmount().add(amount);
        int status=0;
        String remark="";
        if (BigDecimalUtil.eq(add,record.getCommissionAmount())){
            status=2;
        } else {
            status=1;
        }
        remark=String.format("已解锁%s",add.toPlainString());

        int i = creditCardCommissionRecordService.unLock(record.getId(), amount,status,remark);
        Assert.isTrue(i>0,"解锁失败");
        //增加到余额
        MemberWallet wallet = memberWalletService.findByCoinAndMemberId("BT", record.getMemberId());
        memberWalletService.trade(wallet.getId(),amount,BigDecimal.ZERO,amount.negate());
        //资金流水
        memberTransactionService.createTransaction(amount,record.getMemberId(),TransactionType.CREDIT_CARD_COMMISSION_RELEASE,"代还手续费返还释放");

    }

    public CreditCardCommissionServiceImpl getService(){
        return SpringContextUtil.getBean(CreditCardCommissionServiceImpl.class);
    }

    private void authenticSignature(CreditCardCommissionRefundVO vo) {
        String format = String.format("%s%s%s%s", cipher, vo.getCommissionAmount(), vo.getMobilePhone(), vo.getTimestamp());
        String md5Encode = MD5Util.md5Encode(format).toLowerCase();
        if (!md5Encode.equalsIgnoreCase(vo.getSign())) {
            log.error("receiver invalid order:{}", vo);
            throw new BtBankException(CommonMsgCode.INVALID_REQUEST_METHOD);
        }
    }
}
