package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.TurntableInvolvement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * 参与记录表(TurntableInvolvement)表数据库访问层
 *
 * @author biu
 * @since 2020-01-08 13:56:22
 */
@Mapper
@Repository
public interface TurntableInvolvementMapper extends BaseMapper<TurntableInvolvement> {

    @Update("UPDATE `turntable_involvement` SET total = total + #{diff}, surplus = surplus + #{diff}, update_time = NOW() WHERE id = #{id} AND total = #{total}")
    int increment(@Param("id") Long memberId, @Param("diff") Integer diff, @Param("total") Integer total);

    @Update("UPDATE `turntable_involvement` SET surplus = surplus - #{diff}, update_time = NOW() WHERE id = #{id} AND surplus - #{diff} >= 0")
    int decrement(@Param("id") Long memberId, @Param("diff") Integer diff);
}