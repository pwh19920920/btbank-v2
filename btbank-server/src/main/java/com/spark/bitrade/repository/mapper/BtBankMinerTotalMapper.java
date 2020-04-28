package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.BtBankMinerTotal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 挖矿汇总报表(BtBankMinerTotal)表数据库访问层
 *
 * @author zyj
 * @since 2019-12-23 15:11:30
 */
@Mapper
public interface BtBankMinerTotalMapper extends BaseMapper<BtBankMinerTotal> {
    /**
     * 转入本金总额,次数,人数
     */
    BtBankMinerTotal getPrincipal(@Param("startTime") String startTime);

    /**
     * 结算佣金总额
     */
    BigDecimal getReward(@Param("startTime") String startTime);

    /**
     * 结算本金总额
     */
    BigDecimal getSettle(@Param("startTime") String startTime);

    /**
     * 直推、金牌佣金
     */
    Map<String, BigDecimal> getDirectAndGoldRebate(@Param("startTime") String startTime);
}