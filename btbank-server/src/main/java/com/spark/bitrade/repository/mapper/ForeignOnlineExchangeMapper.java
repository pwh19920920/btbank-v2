package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.ForeignOnlineExchange;
import org.apache.ibatis.annotations.Mapper;

/**
 * 换汇线上订单表(ForeignOnlineExchange)表数据库访问层
 *
 * @author yangch
 * @since 2020-02-04 11:49:34
 */
@Mapper
public interface ForeignOnlineExchangeMapper extends BaseMapper<ForeignOnlineExchange> {

}