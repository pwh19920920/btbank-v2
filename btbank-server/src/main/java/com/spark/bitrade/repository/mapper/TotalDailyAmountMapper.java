package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.TotalDailyAmount;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * total_daily_amount 每日用户资产汇总表操作
 *
 * @author qiuyuanjie
 * @since 2020-03-09 16:48:01
 */
@Mapper
public interface TotalDailyAmountMapper extends BaseMapper<TotalDailyAmount> {

    /**
     * 活期宝币数
     * @return
     */
    TotalDailyAmount hqbStatTotal();

    /**
     * 大宗挖矿
     * @return
     */
    TotalDailyAmount bulkMiningTotal();

    /**
     * 矿池可用 矿池锁仓
     * @return
     */
    TotalDailyAmount minerBalanceAmountStat();

    /**
     * 企业矿池
     * @return
     */
    TotalDailyAmount enterpriseMinerStat();

    /**
     * 红包锁仓
     * @return
     */
    TotalDailyAmount redLockAmountStat();

    /**
     * 钱包总额
     * @return
     */
    List<TotalDailyAmount> memberWalletStat();
}
