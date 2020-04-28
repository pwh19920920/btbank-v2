package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.OtcConfigType;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.MemberTransaction;
import com.spark.bitrade.mapper.MemberTransactionMapper;
import com.spark.bitrade.service.IMemberTransactionService;
import com.spark.bitrade.service.IOtcConfigApiService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.vo.ProfitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * (MemberTransaction)表服务实现类
 *
 * @author yangch
 * @since 2019-06-15 16:27:30
 */
@Service("memberTransactionService")
public class MemberTransactionServiceImpl extends ServiceImpl<MemberTransactionMapper, MemberTransaction> implements IMemberTransactionService {

    @Autowired
    private MemberTransactionMapper memberTransactionMapper;
    @Autowired
    private IOtcConfigApiService otcConfigApiService;

    @Override
    public ProfitVo profitCount(Long memberId) {
        LocalDateTime now = LocalDateTime.now().plusDays(-1);
        now = now.withHour(16).withMinute(0).withSecond(0);
        LocalDateTime yesterday = now.plusDays(-1);
        Double yesterdayProfit = memberTransactionMapper.selectProfitCount(memberId,yesterday,now);
        LocalDateTime threeMonthsAgo = now.plusMonths(-3);
        Double totalProfit = memberTransactionMapper.selectProfitCount(memberId,threeMonthsAgo,LocalDateTime.now());
        ProfitVo profitVo = new ProfitVo();
        profitVo.setYesterdayProfit((yesterdayProfit == null?BigDecimal.ZERO:new BigDecimal(yesterdayProfit)));
        profitVo.setTotalProfit((totalProfit == null?BigDecimal.ZERO:new BigDecimal(totalProfit)));
        String val = otcConfigApiService.getValue(OtcConfigType.OTC_MINER_COMMISSION_RATE).getData();
        if(null != val){
            profitVo.setOrderRevenueratio(new BigDecimal(val));
        }
        return profitVo;
    }

    @Override
    public IPage<MemberTransaction> profitList(Long memberId,int page,int size) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.plusDays(-1);
        LocalDateTime threeMonthsAgo = now.plusMonths(-3);
        IPage<MemberTransaction> iPage = new Page<>(page,size);
        iPage.setRecords(memberTransactionMapper.profitList(iPage,memberId,threeMonthsAgo,now));
        return iPage;
    }


    public boolean createTransaction(BigDecimal amount, Long memberId, TransactionType transactionType, String comment){
        return this.createTransaction(amount,memberId,transactionType,comment,new Date());
    }

    public boolean createTransaction(BigDecimal amount, Long memberId, TransactionType transactionType, String comment,Date date){
        MemberTransaction transaction=new MemberTransaction();
        transaction.setAmount(amount);
        transaction.setCreateTime(date);
        transaction.setMemberId(memberId);
        transaction.setSymbol("BT");
        transaction.setType(transactionType);
        transaction.setFee(BigDecimal.ZERO);
        transaction.setFlag(0);
        transaction.setRefId("");
        transaction.setComment(comment);
        int insert = memberTransactionMapper.insert(transaction);
        AssertUtil.isTrue(insert>0, CommonMsgCode.UNKNOWN_ERROR);
        return insert>0;
    }

}