package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.api.dto.IntKV;
import com.spark.bitrade.repository.entity.WelfareNewQualification;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 新人福利参与资格(WelfareNewQualification)表数据库访问层
 *
 * @author biu
 * @since 2020-04-08 14:17:15
 */
@Mapper
@Repository
public interface WelfareNewQualificationMapper extends BaseMapper<WelfareNewQualification> {

    @Results({
            @Result(column = "k", property = "k", jdbcType = JdbcType.INTEGER),
            @Result(column = "v", property = "v", jdbcType = JdbcType.INTEGER)
    })
    @Select("SELECT w.`status` k , COUNT(1) v FROM `welfare_new_qualification` w WHERE w.`member_id` = #{memberId} GROUP BY w.`status`")
    List<IntKV> countKV(@Param("memberId") Long memberId);

    @Update("UPDATE \n" +
            "  `welfare_new_qualification` w \n" +
            "SET\n" +
            "  w.`status` = 1,\n" +
            "  w.`ref_id` = #{refId},\n" +
            "  w.`update_time` = NOW() \n" +
            "WHERE w.`member_id` = #{memberId} AND w.`status` = 0 ORDER BY w.`create_time` ASC LIMIT 1")
    Integer decrease(@Param("memberId") Long memberId, @Param("refId") String refId);

    Set<String> getExistIds();

    List<WelfareNewQualification> getNewInvolvement(@Param("opening") Date openning, @Param("closing") Date closing);
}