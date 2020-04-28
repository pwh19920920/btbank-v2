package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.api.vo.FinancialActivityJoinDetailsVo;
import org.apache.ibatis.annotations.Mapper;
import com.spark.bitrade.repository.entity.FinancialActivityJoinDetails;

import java.math.BigDecimal;

/**
 * 理财活动参与明细(FinancialActivityJoinDetails)表数据库访问层
 *
 * @author daring5920
 * @since 2019-12-21 11:49:42
 */
@Mapper
public interface FinancialActivityJoinDetailsMapper extends BaseMapper<FinancialActivityJoinDetails> {

    BigDecimal getJoinActivitiesProfit(FinancialActivityJoinDetailsVo financialActivityJoinDetailsVo);


    BigDecimal getTotalLock(FinancialActivityJoinDetailsVo financialActivityJoinDetailsVo);

    Integer getAlreadyJoinNum(FinancialActivityJoinDetailsVo financialActivityJoinDetailsVo);
    FinancialActivityJoinDetailsVo getJoinActivitiesDetail(FinancialActivityJoinDetailsVo financialActivityJoinDetailsVo);

    Integer effectiveMiner(Long inviterId);
}