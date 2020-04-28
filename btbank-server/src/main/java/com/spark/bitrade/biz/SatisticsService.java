package com.spark.bitrade.biz;

/**
 * * @author Administrator * @time 2019.12.16 15:06
 */
public interface SatisticsService {
    /**
     * 矿池订单汇总数据存储
     *
     * @return
     */
    Boolean statMinerOrderTotal();

    /**
     * 挖矿汇总
     *
     * @return
     */
    Boolean statMinerTotal();

    /**
     * 企业挖矿汇总
     *
     * @return
     */
    Boolean statEnterpriseMineTotal();

    /**
     * 用户资产每日汇总
     *
     * @return
     */
    Boolean statTotalDailyAmount();

    /**
     * 业绩排名快照
     *
     * @return
     */
    Boolean statPerRanking();

    /**
     * 单个交易日OTC统计
     *
     * @return
     */
    Boolean statSingleTransactionOTC();

    /**
     * 单个交易日云端转入转出（内部转账）排名统计
     *
     * @return
     */
    Boolean statFastPayRank();

    /**
     * 单个交易日OTC购买统计
     *
     * @return
     */
    Boolean statOTCTransactionBuy();

    /**
     * 单个交易日OTC出售统计
     *
     * @return
     */
    Boolean statOTCTransactionSell();
}
