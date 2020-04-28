package com.spark.bitrade.repository.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.vo.FinancialActivityJoinDetailsVo;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.mapper.FinancialActivityJoinDetailsMapper;
import com.spark.bitrade.repository.entity.FinancialActivityJoinDetails;
import com.spark.bitrade.repository.service.FinancialActivityJoinDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 理财活动参与明细(FinancialActivityJoinDetails)表服务实现类
 *
 * @author daring5920
 * @since 2019-12-21 11:49:42
 */
@Service("financialActivityJoinDetailsService")
public class FinancialActivityJoinDetailsServiceImpl extends ServiceImpl<FinancialActivityJoinDetailsMapper, FinancialActivityJoinDetails> implements FinancialActivityJoinDetailsService {
    @Autowired
    private  FinancialActivityJoinDetailsMapper financialActivityJoinDetailsMapper;
    @Override
    public List<FinancialActivityJoinDetails> getAvailableActivities() {
        return null;
    }

    @Override
    public List<FinancialActivityJoinDetails> getJoinActivities(Member member, int type) {
        return null;
    }

    @Override
    public BigDecimal getJoinActivitiesProfit(FinancialActivityJoinDetailsVo financialActivityJoinDetailsVo) {
        return financialActivityJoinDetailsMapper.getJoinActivitiesProfit(financialActivityJoinDetailsVo);
    }

    @Override
    public FinancialActivityJoinDetailsVo getJoinActivitiesDetail(FinancialActivityJoinDetailsVo financialActivityJoinDetailsVo) {
        return financialActivityJoinDetailsMapper.getJoinActivitiesDetail(financialActivityJoinDetailsVo);
    }

    @Override
    public BigDecimal getTotalLock(FinancialActivityJoinDetailsVo financialActivityJoinDetailsVo) {
        return financialActivityJoinDetailsMapper.getTotalLock(financialActivityJoinDetailsVo);
    }

    @Override
    public Integer getAlreadyJoinNum(FinancialActivityJoinDetailsVo financialActivityJoinDetailsVo) {
        return financialActivityJoinDetailsMapper.getAlreadyJoinNum(financialActivityJoinDetailsVo);
    }

    @Override
    public boolean effectiveMiner(Long inviterId, BigDecimal tranferAmountparm) {
       Integer count  =  financialActivityJoinDetailsMapper.effectiveMiner(inviterId);
       if(count>0){
           return Boolean.TRUE;
       }
        return Boolean.FALSE;
    }

}