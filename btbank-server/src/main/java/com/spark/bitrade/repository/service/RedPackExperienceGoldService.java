package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.RedPackExperienceGold;

import java.math.BigDecimal;

/**
 * 红包体检金流水表(RedPackExperienceGold)表服务接口
 *
 * @author daring5920
 * @since 2019-12-08 10:44:35
 */
public interface RedPackExperienceGoldService extends IService<RedPackExperienceGold> {

    BigDecimal getRedBagLockAmount(Long memberId);

    Boolean saveGetId(RedPackExperienceGold redPackExperienceGold);
}