package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.BtBankMinerTotal;
import com.spark.bitrade.repository.mapper.BtBankMinerTotalMapper;
import com.spark.bitrade.repository.service.BtBankMinerTotalService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 挖矿汇总报表(BtBankMinerTotal)表服务实现类
 *
 * @author zyj
 * @since 2019-12-23 15:11:30
 */
@Service("btBankMinerTotalService")
public class BtBankMinerTotalServiceImpl extends ServiceImpl<BtBankMinerTotalMapper, BtBankMinerTotal> implements BtBankMinerTotalService {

    @Override
    public BtBankMinerTotal getPrincipal(String startTime) {
        return baseMapper.getPrincipal(startTime);
    }

    @Override
    public BigDecimal getReward(String startTime) {
        return baseMapper.getReward(startTime);
    }

    @Override
    public BigDecimal getSettle(String startTime) {
        return baseMapper.getSettle(startTime);
    }

    @Override
    public Map<String, BigDecimal> getDirectAndGoldRebate(String startTime) {
        return baseMapper.getDirectAndGoldRebate(startTime);
    }
}