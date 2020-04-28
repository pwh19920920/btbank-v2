package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.ForeignCashLocation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 外汇线下换汇地址(ForeignCashLocation)表数据库访问层
 *
 * @author yangch
 * @since 2020-02-04 11:35:35
 */
@Mapper
public interface ForeignCashLocationMapper extends BaseMapper<ForeignCashLocation> {

}