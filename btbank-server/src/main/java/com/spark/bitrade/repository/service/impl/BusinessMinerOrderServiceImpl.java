package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.BusinessMinerOrder;
import com.spark.bitrade.repository.mapper.BusinessMinerOrderMapper;
import com.spark.bitrade.repository.service.BusinessMinerOrderService;
import com.spark.bitrade.repository.service.OtcOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 商家矿池订单表(BusinessMinerOrder)表服务实现类
 *
 * @author daring5920
 * @since 2019-11-27 17:53:28
 */
@Slf4j
@Service("businessMinerOrderService")
public class BusinessMinerOrderServiceImpl extends ServiceImpl<BusinessMinerOrderMapper, BusinessMinerOrder> implements BusinessMinerOrderService {

    private OtcOrderService otcOrderService;

    @Autowired
    public void setOtcOrderService(OtcOrderService otcOrderService) {
        this.otcOrderService = otcOrderService;
    }

    @Override
    public int countInProgress(Long memberId) {
        QueryWrapper<BusinessMinerOrder> query = new QueryWrapper<>();
        query.eq("sell_id", memberId).in("status", 0, 1, 2);

        return count(query) + otcOrderService.countOrders(memberId, false);
    }

    @Override
    @Transactional
    public void updateQueueStatus(Integer size) {
        List<Long> queueOrders = baseMapper.findQueueOrders(size);
        log.info("更新id:{}",queueOrders);
        if(CollectionUtils.isEmpty(queueOrders)){
            return;
        }
        baseMapper.updateQueueStatus(queueOrders);
    }


}