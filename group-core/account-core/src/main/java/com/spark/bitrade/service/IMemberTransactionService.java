package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.entity.MemberTransaction;
import com.spark.bitrade.vo.ProfitVo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * (MemberTransaction)表服务接口
 *
 * @author yangch
 * @since 2019-06-15 16:27:30
 */
public interface IMemberTransactionService extends IService<MemberTransaction> {

    /**
     * 昨日收益与累计收益统计
     * @param memberId
     * @return
     */
    ProfitVo profitCount(Long memberId);

    IPage<MemberTransaction> profitList(Long memberId, int page, int size);

    /**
     * 生成资金流水
     * @param amount
     * @param memberId
     * @param transactionType
     * @param comment
     * @return
     */
    boolean createTransaction(BigDecimal amount, Long memberId, TransactionType transactionType, String comment);

    boolean createTransaction(BigDecimal amount, Long memberId, TransactionType transactionType, String comment, Date date);
}