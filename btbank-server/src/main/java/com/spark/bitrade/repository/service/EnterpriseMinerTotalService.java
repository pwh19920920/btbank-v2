package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.EnterpriseMinerTotal;

/**
 * 企业挖矿汇总表(EnterpriseMinerTotal)表服务接口
 *
 * @author zyj
 * @since 2019-12-27 11:33:00
 */
public interface EnterpriseMinerTotalService extends IService<EnterpriseMinerTotal> {

    /**
     * 转入次数、人数、总额
     *
     * @param startTime
     * @return
     */
    EnterpriseMinerTotal getInto(String startTime);

    /**
     * 转出、挖矿、佣金
     *
     * @param startTime
     * @return
     */
    EnterpriseMinerTotal getSendAndMineAndReward(String startTime);
}