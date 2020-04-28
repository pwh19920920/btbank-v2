package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.RedPackLock;
import org.apache.ibatis.annotations.Mapper;

/**
 * 红包锁仓表(RedPackLock)表数据库访问层
 *
 * @author daring5920
 * @since 2019-12-08 10:44:37
 */
@Mapper
public interface RedPackLockMapper extends BaseMapper<RedPackLock> {

}