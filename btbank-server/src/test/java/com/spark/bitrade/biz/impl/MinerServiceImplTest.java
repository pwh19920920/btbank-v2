package com.spark.bitrade.biz.impl;

import com.spark.bitrade.constant.ApplyGoldMinerCode;
import com.spark.bitrade.constant.MinerGradeNoteType;
import com.spark.bitrade.repository.entity.BtBankMinerBalance;
import com.spark.bitrade.repository.entity.BtBankMinerGradeNote;
import com.spark.bitrade.repository.service.BtBankMinerBalanceService;
import com.spark.bitrade.repository.service.BtBankMinerGradeNoteService;
import com.spark.bitrade.trans.Tuple2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class MinerServiceImplTest {
    @InjectMocks
    MinerServiceImpl minerService = new MinerServiceImpl();

    @Mock
    BtBankMinerBalanceService minerBalanceService;

    @Mock
    BtBankMinerGradeNoteService minerGradeNoteService;

    @Test
    public void tryApplyUpgradeToGoldAlready() {
        BtBankMinerBalance minerBalance = new BtBankMinerBalance();
        minerBalance.setMemberId(1L);
        minerBalance.setMinerGrade(2);
        BDDMockito.given(minerBalanceService.findFirstByMemberId(1L)).willReturn(minerBalance);
        Tuple2<ApplyGoldMinerCode, String> result = minerService.tryApplyUpgradeToGold(1L);
        Assert.assertEquals(result.getFirst(), ApplyGoldMinerCode.ALREADY_GOLD_MINER);
    }

    @Test
    public void testGoldNotSilver() {
        BtBankMinerBalance minerBalance = new BtBankMinerBalance();
        minerBalance.setMemberId(1L);
        minerBalance.setMinerGrade(0);
        BDDMockito.given(minerBalanceService.findFirstByMemberId(1L)).willReturn(minerBalance);

        BDDMockito.given(minerGradeNoteService.findLastRecordByMemberId(1L)).willReturn(null);

        Tuple2<ApplyGoldMinerCode, String> result = minerService.tryApplyUpgradeToGold(1L);
        Assert.assertEquals(result.getFirst(), ApplyGoldMinerCode.Ineligible);
    }

    @Test
    public void testGoldNotCount() {
        BtBankMinerBalance minerBalance = new BtBankMinerBalance();
        minerBalance.setMemberId(1L);
        minerBalance.setMinerGrade(1);
        BDDMockito.given(minerBalanceService.findFirstByMemberId(1L)).willReturn(minerBalance);

        BDDMockito.given(minerGradeNoteService.findLastRecordByMemberId(1L)).willReturn(null);

        Tuple2<ApplyGoldMinerCode, String> result = minerService.tryApplyUpgradeToGold(1L);
        Assert.assertEquals(result.getFirst(), ApplyGoldMinerCode.Ineligible);
    }

    @Test
    public void testGoldPending() {
        BtBankMinerBalance minerBalance = new BtBankMinerBalance();
        minerBalance.setMemberId(1L);
        minerBalance.setMinerGrade(0);
        BDDMockito.given(minerBalanceService.findFirstByMemberId(1L)).willReturn(minerBalance);
        BtBankMinerGradeNote note = new BtBankMinerGradeNote();
        note.setIsOperation(false);
        note.setType(MinerGradeNoteType.PENDING.getCode());
        BDDMockito.given(minerGradeNoteService.findLastRecordByMemberId(1L)).willReturn(note);

        Tuple2<ApplyGoldMinerCode, String> result = minerService.tryApplyUpgradeToGold(1L);
        Assert.assertEquals(result.getFirst(), ApplyGoldMinerCode.PENDING);
    }

    @Test
    public void testGoldRefused() {
        BtBankMinerBalance minerBalance = new BtBankMinerBalance();
        minerBalance.setMemberId(1L);
        minerBalance.setMinerGrade(0);
        BDDMockito.given(minerBalanceService.findFirstByMemberId(1L)).willReturn(minerBalance);
        BtBankMinerGradeNote note = new BtBankMinerGradeNote();
        note.setIsOperation(true);
        note.setType(MinerGradeNoteType.FAILD.getCode());
        BDDMockito.given(minerGradeNoteService.findLastRecordByMemberId(1L)).willReturn(note);

        Tuple2<ApplyGoldMinerCode, String> result = minerService.tryApplyUpgradeToGold(1L);
        Assert.assertEquals(result.getFirst(), ApplyGoldMinerCode.APPLY_FAILED);
    }
}