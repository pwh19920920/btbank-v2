package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.ForeignConfigDataDict;
import org.apache.ibatis.annotations.Mapper;

/**
 * btbank外汇规则配置(ForeignConfigDataDict)表数据库访问层
 *
 * @author yangch
 * @since 2020-02-04 11:43:23
 */
@Mapper
public interface ForeignConfigDataDictMapper extends BaseMapper<ForeignConfigDataDict> {

}