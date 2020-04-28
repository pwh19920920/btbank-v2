package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.OtcCoin;
import org.apache.ibatis.annotations.Mapper;

/**
 * (OtcCoin)表数据库访问层
 *
 * @author daring5920
 * @since 2019-11-29 15:55:58
 */
@Mapper
public interface OtcCoinMapper extends BaseMapper<OtcCoin> {

}