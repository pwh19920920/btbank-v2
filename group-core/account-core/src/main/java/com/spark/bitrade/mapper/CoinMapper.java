package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.Coin;
import org.apache.ibatis.annotations.Mapper;

/**
 * (Coin)表数据库访问层
 *
 * @author zhangYanjun
 * @since 2019-06-20 17:48:00
 */
@Mapper
public interface CoinMapper extends BaseMapper<Coin> {

}