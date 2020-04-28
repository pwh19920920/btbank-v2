package com.spark.bitrade.repository.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.api.vo.FinancialActivityJoinDetailsVo;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.FinancialActivityJoinDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 理财活动参与明细(FinancialActivityJoinDetails)表服务接口
 *
 * @author daring5920
 * @since 2019-12-21 11:49:42
 */
public interface FinancialActivityJoinDetailsService extends IService<FinancialActivityJoinDetails> {

    List<FinancialActivityJoinDetails> getAvailableActivities();

    List<FinancialActivityJoinDetails> getJoinActivities(Member member,int type);

    BigDecimal getJoinActivitiesProfit(FinancialActivityJoinDetailsVo financialActivityJoinDetailsVo) ;

    FinancialActivityJoinDetailsVo getJoinActivitiesDetail(FinancialActivityJoinDetailsVo financialActivityJoinDetailsVo);
    BigDecimal getTotalLock(FinancialActivityJoinDetailsVo financialActivityJoinDetailsVo );
    Integer getAlreadyJoinNum(FinancialActivityJoinDetailsVo financialActivityJoinDetailsVo);

    boolean effectiveMiner(Long inviterId, BigDecimal tranferAmountparm);
}