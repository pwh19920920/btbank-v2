package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.BusinessMinerOrder;

/**
 * 商家矿池订单表(BusinessMinerOrder)表服务接口
 *
 * @author daring5920
 * @since 2019-11-27 17:53:28
 */
public interface BusinessMinerOrderService extends IService<BusinessMinerOrder> {

    /**
     * 统计处理中的订单
     *
     * @param memberId id
     * @return count
     */
    int countInProgress(Long memberId);

    /**
     * 更新排队状态
     */
    void updateQueueStatus(Integer size);
}