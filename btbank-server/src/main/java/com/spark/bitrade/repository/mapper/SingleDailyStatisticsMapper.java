package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.SingleDailyStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (SingleDailyStatistics)表数据库访问层
 *
 * @author daring5920
 * @since 2020-03-23 13:42:21
 */
@Mapper
public interface SingleDailyStatisticsMapper extends BaseMapper<SingleDailyStatistics> {

    /**
     * 单个交易日OTC购买排名
     *
     * @return
     */
    List<SingleDailyStatistics> buyOTCRank(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 单个交易日OTC出售排名
     *
     * @return
     */
    List<SingleDailyStatistics> sellOTCRank(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 单个交易日云端转入（内部转账）排名
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<SingleDailyStatistics> findPayFastReceiveRank(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 单个交易日云端转出（内部转账）排名
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<SingleDailyStatistics> findPayFastPayRank(@Param("startTime") String startTime, @Param("endTime") String endTime);

}