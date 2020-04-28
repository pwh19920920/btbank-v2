package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.OtcOrderSubsidyPerDay;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户补贴记录表(OtcOrderSubsidyPerDay)表数据库访问层
 *
 * @author daring5920
 * @since 2019-12-02 11:58:36
 */
@Mapper
public interface OtcOrderSubsidyPerDayMapper extends BaseMapper<OtcOrderSubsidyPerDay> {

}