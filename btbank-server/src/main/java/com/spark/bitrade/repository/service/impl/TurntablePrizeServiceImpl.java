package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.TurntablePrize;
import com.spark.bitrade.repository.mapper.TurntablePrizeMapper;
import com.spark.bitrade.repository.service.TurntablePrizeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 奖品表(TurntablePrize)表服务实现类
 *
 * @author biu
 * @since 2020-01-08 13:56:37
 */
@Service("turntablePrizeService")
public class TurntablePrizeServiceImpl extends ServiceImpl<TurntablePrizeMapper, TurntablePrize> implements TurntablePrizeService {

    @Override
    public List<TurntablePrize> getPrizes(Integer actId) {
        QueryWrapper<TurntablePrize> wrapper = new QueryWrapper<>();
        wrapper.eq("act_id", actId);

        return list(wrapper);
    }

    @Override
    public boolean decrement(Integer prizeId) {
        return baseMapper.decrement(prizeId) > 0;
    }
}