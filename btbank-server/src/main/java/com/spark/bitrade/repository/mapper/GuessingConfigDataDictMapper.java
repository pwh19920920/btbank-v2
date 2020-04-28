package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.spark.bitrade.repository.entity.GuessingConfigDataDict;
import org.apache.ibatis.annotations.Param;

/**
 * 竞猜活动规则配置(GuessingConfigDataDict)表数据库访问层
 *
 * @author daring5920
 * @since 2020-01-02 10:30:50
 */
@Mapper
public interface GuessingConfigDataDictMapper extends BaseMapper<GuessingConfigDataDict> {
    GuessingConfigDataDict findFirstByDictIdAndDictKey(@Param("dictId") String dictId, @Param("dictKey") String dictKey);
}