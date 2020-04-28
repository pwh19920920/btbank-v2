package com.spark.bitrade.biz;

import java.util.Date;

/**
 * MinerRebateCheckService
 *
 * @author biu
 * @since 2019/12/9 17:54
 */
public interface MinerRebateCheckService {

    /**
     * 检查奖励发放
     */
    void checkRebate(Date begin);
}
