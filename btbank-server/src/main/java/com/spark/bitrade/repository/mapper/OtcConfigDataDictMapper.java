package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.OtcConfigDataDict;
import org.apache.ibatis.annotations.Mapper;

/**
 * btbank规则配置(OtcConfigDataDict)表数据库访问层
 *
 * @author daring5920
 * @since 2019-11-27 17:53:30
 */
@Mapper
public interface OtcConfigDataDictMapper extends BaseMapper<OtcConfigDataDict> {

}