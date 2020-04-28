package com.spark.bitrade.biz;

import com.spark.bitrade.api.vo.EnterpriseOrderVo;
import com.spark.bitrade.repository.entity.EnterpriseMinerTransaction;

/**
 * EnterpriseMiningService
 *
 * @author biu
 * @since 2019/12/24 13:35
 */
public interface EnterpriseMiningService {

    EnterpriseMinerTransaction mining(EnterpriseOrderVo orderVo);

    /**
     * 处理流水，异步
     *
     * @param tx tx
     */
    void handle(EnterpriseMinerTransaction tx);

    /**
     * 归集
     *
     * @param tx tx
     */
    void collect(EnterpriseMinerTransaction tx);

    /**
     * 发放奖励
     *
     * @param tx tx
     */
    void reward(EnterpriseMinerTransaction tx);
}
