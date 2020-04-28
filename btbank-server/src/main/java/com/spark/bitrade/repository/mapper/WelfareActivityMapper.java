package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.WelfareActivity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 福利包活动(WelfareActivity)表数据库访问层
 *
 * @author biu
 * @since 2020-04-08 14:14:41
 */
@Mapper
@Repository
public interface WelfareActivityMapper extends BaseMapper<WelfareActivity> {

}