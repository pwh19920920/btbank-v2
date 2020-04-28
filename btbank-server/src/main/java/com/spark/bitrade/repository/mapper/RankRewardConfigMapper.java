package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.spark.bitrade.repository.entity.RankRewardConfig;

/**
 * 奖励金额配置(RankRewardConfig)表数据库访问层
 *
 * @author daring5920
 * @since 2019-12-17 18:08:24
 */
@Mapper
public interface RankRewardConfigMapper extends BaseMapper<RankRewardConfig> {

}