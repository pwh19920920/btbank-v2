package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.EnterpriseMinerTotal;
import com.spark.bitrade.repository.mapper.EnterpriseMinerTotalMapper;
import com.spark.bitrade.repository.service.EnterpriseMinerTotalService;
import org.springframework.stereotype.Service;

/**
 * 企业挖矿汇总表(EnterpriseMinerTotal)表服务实现类
 *
 * @author zyj
 * @since 2019-12-27 11:33:00
 */
@Service("enterpriseMinerTotalService")
public class EnterpriseMinerTotalServiceImpl extends ServiceImpl<EnterpriseMinerTotalMapper, EnterpriseMinerTotal> implements EnterpriseMinerTotalService {

    @Override
    public EnterpriseMinerTotal getInto(String startTime) {
        return baseMapper.getInto(startTime);
    }

    @Override
    public EnterpriseMinerTotal getSendAndMineAndReward(String startTime) {
        return baseMapper.getSendAndMineAndReward(startTime);
    }
}