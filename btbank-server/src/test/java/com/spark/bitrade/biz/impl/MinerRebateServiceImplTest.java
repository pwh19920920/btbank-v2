package com.spark.bitrade.biz.impl;

import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.BtBankMinerBalance;
import com.spark.bitrade.repository.entity.BtBankMinerBalanceTransaction;
import com.spark.bitrade.repository.service.BtBankMinerBalanceService;
import com.spark.bitrade.repository.service.BtBankMinerBalanceTransactionService;
import com.spark.bitrade.repository.service.BtBankRebateRecordService;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.service.MemberAccountService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.IdWorkByTwitter;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@RunWith(SpringRunner.class)
public class MinerRebateServiceImplTest {

    @InjectMocks
    MinerRebateServiceImpl minerRebateService;

    @Mock
    IdWorkByTwitter idWorkByTwitter;

    @Mock
    BtBankRebateRecordService rebateRecordService;

    @Mock
    MemberAccountService memberAccountService;

    @Mock
    BtBankConfigService configService;

    @Mock
    MemberWalletService memberWalletService;

    @Mock
    BtBankMinerBalanceService minerBalanceService;

    @Mock
    BtBankMinerBalanceTransactionService minerBalanceTransactionService;

    @Test
    public void processRebateRecord() {
        BDDMockito.given(configService.getConfig(BtBankSystemConfig.SILVER_MINER_COMMISSION_RATE)).willReturn("0.1");
        BDDMockito.given(configService.getConfig(BtBankSystemConfig.GOLD_MINER_COMMISSION_RATE)).willReturn("0.2");
        BDDMockito.given(minerBalanceTransactionService.markRebateProcessedById(BDDMockito.anyLong())).willReturn(Boolean.TRUE);

        MessageRespResult<Boolean> respResult = new MessageRespResult<>();
        respResult.setData(Boolean.TRUE);
        BDDMockito.given(memberWalletService.optionMemberWalletBalance(
                TransactionType.TRANSFER_ACCOUNTS,
                BDDMockito.anyLong(), BDDMockito.anyString(), BDDMockito.anyString(), BDDMockito.anyObject(),
                BDDMockito.anyLong(), BDDMockito.anyString())).willReturn(respResult);
        Member self = new Member();
        self.setId(3L);
        self.setInviterId(2L);
        BDDMockito.given(memberAccountService.findMemberByMemberId(self.getId())).willReturn(self);
        Member father = new Member();
        father.setId(2L);
        father.setInviterId(1L);
        BDDMockito.given(memberAccountService.findMemberByMemberId(father.getId())).willReturn(father);
        Member grandfather = new Member();
        grandfather.setId(1L);
        grandfather.setInviterId(null);
        BDDMockito.given(memberAccountService.findMemberByMemberId(grandfather.getId())).willReturn(grandfather);

        BDDMockito.given(idWorkByTwitter.nextId()).willReturn(324234234234234L);
        BDDMockito.given(rebateRecordService.save(BDDMockito.anyObject())).willReturn(Boolean.TRUE);

        BtBankMinerBalance fatherMinerBalance = new BtBankMinerBalance();
        fatherMinerBalance.setMemberId(father.getId());
        fatherMinerBalance.setMinerGrade(2);
        BDDMockito.given(minerBalanceService.findFirstByMemberId(father.getId())).willReturn(fatherMinerBalance);

        BtBankMinerBalance grandfatherMinerBalance = new BtBankMinerBalance();
        grandfatherMinerBalance.setMemberId(grandfather.getId());
        grandfatherMinerBalance.setMinerGrade(2);
        BDDMockito.given(minerBalanceService.findFirstByMemberId(grandfather.getId())).willReturn(grandfatherMinerBalance);

        BtBankMinerBalanceTransaction tx = new BtBankMinerBalanceTransaction();
        tx.setId(1573478669202L);
        tx.setMemberId(3L);
        tx.setType(4);
        tx.setMoney(BigDecimal.valueOf(65.1));
        MemberWalletService.TradePlan plan = new MemberWalletService.TradePlan();
        minerRebateService.processRebateRecord(tx, plan);
        long l = idWorkByTwitter.nextId();
        log.info("idWorkByTwitter.nextId():{}", l);
    }

    @Test
    public void testStream() {
        Collection<Boolean> collection = Arrays.asList(Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
        Optional<Boolean> any = collection.stream().filter(x -> !x).findAny();
        log.info("{} {}", any, any.orElse(Boolean.TRUE));
        Assert.isTrue(any.orElse(Boolean.TRUE), "必须是真");
    }
}