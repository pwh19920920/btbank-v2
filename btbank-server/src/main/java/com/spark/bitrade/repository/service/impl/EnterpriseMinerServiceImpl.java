package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.EnterpriseMiner;
import com.spark.bitrade.repository.mapper.EnterpriseMinerMapper;
import com.spark.bitrade.repository.service.EnterpriseMinerService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 企业矿工表(EnterpriseMiner)表服务实现类
 *
 * @author biu
 * @since 2019-12-23 17:15:02
 */
@Service("enterpriseMinerService")
public class EnterpriseMinerServiceImpl extends ServiceImpl<EnterpriseMinerMapper, EnterpriseMiner> implements EnterpriseMinerService {

    @Override
    public EnterpriseMiner findByMemberId(Long memberId) {
        return baseMapper.findByMemberId(memberId);
    }

    @Override
    public boolean transfer(Long memberId, BigDecimal amount) {
        return baseMapper.transfer(memberId, amount) > 0;
    }

    @Override
    public boolean mining(Integer minerId, BigDecimal amount) {
        return baseMapper.mining(minerId, amount.abs()) > 0;
    }

    @Override
    public boolean reward(Integer minerId, BigDecimal amount) {
        return baseMapper.reward(minerId, amount.abs()) > 0;
    }
}