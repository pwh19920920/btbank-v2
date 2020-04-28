package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.BtBankMinerTotal;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 挖矿汇总报表(BtBankMinerTotal)表服务接口
 *
 * @author zyj
 * @since 2019-12-23 15:11:30
 */
public interface BtBankMinerTotalService extends IService<BtBankMinerTotal> {
    /**
     * 转入本金总额,次数,人数
     *
     * @param startTime
     * @return
     */
    BtBankMinerTotal getPrincipal(String startTime);

    /**
     * 结算佣金总额
     *
     * @param startTime
     * @return
     */
    BigDecimal getReward(String startTime);

    /**
     * 结算本金总额
     *
     * @param startTime
     * @return
     */
    BigDecimal getSettle(String startTime);

    /**
     * 直推、金牌佣金
     *
     * @param startTime
     * @return
     */
    Map<String, BigDecimal> getDirectAndGoldRebate(String startTime);
}