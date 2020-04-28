package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.SingleDealStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * 单个交易日OTC统计(RankRewardTransaction)表数据库访问层
 *
 * @author qiuyuanjie
 * @since 2020-03-25 15:14:10
 */
@Mapper
public interface SingleDealStatisticsMapper extends BaseMapper<SingleDealStatistics> {

    /**
     * 统计单个交易OTC的出售
     */
    SingleDealStatistics statisticsOtcSell(@Param("startTime")String startTime, @Param("endTime")String endTime);

    /**
     * 统计单个交易OTC的购买
     * @return
     */
    SingleDealStatistics statisticsOtcBuy(@Param("startTime")String startTime,@Param("endTime")String endTime);
}
