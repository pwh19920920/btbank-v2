package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.BtBankMinerOrderTotal;

import java.math.BigDecimal;

/**
 * 矿池订单汇总报表(BtBankMinerOrderTotal)表服务接口
 *
 * @author zyj
 * @since 2019-12-16 14:55:03
 */
public interface BtBankMinerOrderTotalService extends IService<BtBankMinerOrderTotal> {

    /**
     * 统计列表
     *
     * @param startTime
     * @return
     */
    BtBankMinerOrderTotal grabAndSendTotalList(String startTime);

    /**
     * 按天统计：固定收益人数、次数、总额
     *
     * @param startTime
     * @return
     */
    BtBankMinerOrderTotal getFixList(String startTime);

    /**
     * 当日矿池总额
     *
     * @param startTime
     * @return
     */
    BigDecimal getMineTotalByDay(String startTime);


}