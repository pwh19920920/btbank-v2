package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.api.vo.MinerBalanceTransactionsVO;
import com.spark.bitrade.repository.entity.BtBankMinerBalanceTransaction;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface BtBankMinerBalanceTransactionService extends IService<BtBankMinerBalanceTransaction> {


    //按用户查询所有
    MinerBalanceTransactionsVO getMinerBalanceTransactionsByMemberId(Long memberId, List<Integer> types, int page, int size,String range);

    int spendBalanceWithIdAndBalance(Long id, BigDecimal payDecimal);

    BigDecimal getYestodayMinerBalanceTransactionsSumByMemberId(Long memberId, List types);

    List<BtBankMinerBalanceTransaction> listNeedRebate();

    boolean markRebateProcessedById(Long id);

    /**
     * 3月8日活动  统计矿工累计收益
     * type 值为 4 7 9
     */
    List<BtBankMinerBalanceTransaction> countProfitByType(Date limitTime);

    BigDecimal sum38AfterTransfer(Long memberId, Date date);

    /**
     * 根据memberId, 查询 type = 1
     * @param memberId
     * @return
     */
    BtBankMinerBalanceTransaction findByTypeAndMemberId(Long memberId);

    Long[] getValidMiners();
}


