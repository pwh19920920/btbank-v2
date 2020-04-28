package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.ForeignOfflineExchange;
import org.apache.ibatis.annotations.Mapper;

/**
 * 换汇线下订单(ForeignOfflineExchange)表数据库访问层
 *
 * @author yangch
 * @since 2020-02-04 11:48:53
 */
@Mapper
public interface ForeignOfflineExchangeMapper extends BaseMapper<ForeignOfflineExchange> {

}