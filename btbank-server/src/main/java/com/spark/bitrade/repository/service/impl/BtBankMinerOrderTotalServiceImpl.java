package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.BtBankMinerOrderTotal;
import com.spark.bitrade.repository.mapper.BtBankMinerOrderTotalMapper;
import com.spark.bitrade.repository.service.BtBankMinerOrderTotalService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 矿池订单汇总报表(BtBankMinerOrderTotal)表服务实现类
 *
 * @author zyj
 * @since 2019-12-16 14:55:03
 */
@Service("btBankMinerOrderTotalService")
public class BtBankMinerOrderTotalServiceImpl extends ServiceImpl<BtBankMinerOrderTotalMapper, BtBankMinerOrderTotal> implements BtBankMinerOrderTotalService {


    @Override
    public BtBankMinerOrderTotal grabAndSendTotalList(String startTime) {
        return baseMapper.grabAndSendTotalList(startTime);
    }


    @Override
    public BtBankMinerOrderTotal getFixList(String startTime) {
        return baseMapper.fixList(startTime);
    }


    @Override
    public BigDecimal getMineTotalByDay(String startTime) {
        return baseMapper.mineTotalByDay(startTime);
    }

}