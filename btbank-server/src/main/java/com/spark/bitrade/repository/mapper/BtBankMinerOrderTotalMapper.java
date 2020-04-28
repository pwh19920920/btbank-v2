package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.BtBankMinerOrderTotal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 矿池订单汇总报表(BtBankMinerOrderTotal)表数据库访问层
 *
 * @author zyj
 * @since 2019-12-16 14:55:03
 */
@Mapper
public interface BtBankMinerOrderTotalMapper extends BaseMapper<BtBankMinerOrderTotal> {

    /**
     * 查询矿池订单列表
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<BtBankMinerOrderTotal> findList(@Param("startTime") String startTime, @Param("endTime") String endTime);


    /**
     * 按天统计：抢单、派单的人数、次数、总额
     *
     * @param startTime
     * @return
     */
    BtBankMinerOrderTotal grabAndSendTotalList(@Param("startTime") String startTime);


    /**
     * 按天统计：固定收益人数、次数、总额
     *
     * @return
     */
    BtBankMinerOrderTotal fixList(@Param("startTime") String startTime);


    /**
     * 当日矿池总额
     */
    BigDecimal mineTotalByDay(@Param("startTime") String startTime);

}