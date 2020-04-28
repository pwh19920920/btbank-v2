package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.RedPackExperienceGold;
import com.spark.bitrade.repository.mapper.RedPackExperienceGoldMapper;
import com.spark.bitrade.repository.service.RedPackExperienceGoldService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 红包体检金流水表(RedPackExperienceGold)表服务实现类
 *
 * @author daring5920
 * @since 2019-12-08 10:44:35
 */
@Service("redPackExperienceGoldService")
public class RedPackExperienceGoldServiceImpl extends ServiceImpl<RedPackExperienceGoldMapper, RedPackExperienceGold> implements RedPackExperienceGoldService {

    @Override
    public BigDecimal getRedBagLockAmount(Long memberId) {
        return baseMapper.queryRedBagLockAmount(memberId);
    }

    @Override
    public Boolean saveGetId(RedPackExperienceGold redPackExperienceGold) {
        return baseMapper.saveGetId(redPackExperienceGold);
    }


}