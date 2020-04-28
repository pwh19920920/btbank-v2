package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.Advertise;
import org.apache.ibatis.annotations.Mapper;

/**
 * (advertise)表数据库访问层
 *
 * @author qiuyuanjie
 * @since 2020-02-29 10:44:03
 */
@Mapper
public interface AdvertiseMapper extends BaseMapper<Advertise> {
}
