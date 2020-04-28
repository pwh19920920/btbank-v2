package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.TurntablePrize;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * 奖品表(TurntablePrize)表数据库访问层
 *
 * @author biu
 * @since 2020-01-08 13:56:37
 */
@Mapper
@Repository
public interface TurntablePrizeMapper extends BaseMapper<TurntablePrize> {

    @Update("update `turntable_prize` set stock = stock - 1, update_time = now() where id = #{id} and stock - 1 >= 0")
    int decrement(@Param("id") Integer prizeId);
}