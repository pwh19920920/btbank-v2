package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.TurntableActivities;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * 活动表(TurntableActivities)表数据库访问层
 *
 * @author biu
 * @since 2020-01-08 13:56:07
 */
@Mapper
@Repository
public interface TurntableActivitiesMapper extends BaseMapper<TurntableActivities> {

    @Select("select * from `turntable_activities` where start_time < now() and end_time > now() order by create_time asc limit 1")
    TurntableActivities getInProcess();

    @Select("select * from `turntable_activities` order by start_time desc limit 1")
    TurntableActivities getTheLatest();
}