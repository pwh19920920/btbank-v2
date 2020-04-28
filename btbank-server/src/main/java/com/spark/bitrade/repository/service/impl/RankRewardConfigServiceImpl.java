package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.mapper.RankRewardConfigMapper;
import com.spark.bitrade.repository.entity.RankRewardConfig;
import com.spark.bitrade.repository.service.RankRewardConfigService;
import org.springframework.stereotype.Service;

/**
 * 奖励金额配置(RankRewardConfig)表服务实现类
 *
 * @author daring5920
 * @since 2019-12-17 18:08:24
 */
@Service("rankRewardConfigService")
public class RankRewardConfigServiceImpl extends ServiceImpl<RankRewardConfigMapper, RankRewardConfig> implements RankRewardConfigService {

}