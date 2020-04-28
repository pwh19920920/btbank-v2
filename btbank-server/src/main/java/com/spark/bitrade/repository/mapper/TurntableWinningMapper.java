package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.TurntableWinning;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (TurntableWinning)表数据库访问层
 *
 * @author biu
 * @since 2020-01-08 15:11:15
 */
@Mapper
@Repository
public interface TurntableWinningMapper extends BaseMapper<TurntableWinning> {

}