package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.AdvertiseOperationHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 广告操作记录表(AdvertiseOperationHistory)表数据库访问层
 *
 * @author daring5920
 * @since 2019-11-27 17:53:22
 */
@Mapper
public interface AdvertiseOperationHistoryMapper extends BaseMapper<AdvertiseOperationHistory> {

    @Select("select aoh.advertise_id from advertise_operation_history aoh LEFT JOIN advertise ad on ad.id = aoh.advertise_id where ad.member_id = #{memberId} group by aoh.advertise_id ")
    List<Long> getMemberAdHistoryIds(@Param("memberId") Long memberId);


    @Select("select * from advertise_operation_history where advertise_id=#{adId} order by create_time")
    List<AdvertiseOperationHistory> getrAdHistroys(@Param("adId") Long adId);

    @Select("select * from advertise_operation_history where member_id=#{memberId} and create_time between #{begin} and #{end} order by create_time")
    List<AdvertiseOperationHistory> listAdHistroysByMemberId(@Param("memberId") Long memberId,@Param("begin") Date begin,@Param("end") Date end);

    @Select("SELECT\n" +
            "\tmprt.relieve_time\n" +
            "FROM\n" +
            "\tmember_permissions_relieve_task mprt\n" +
            "LEFT JOIN monitor_rule_config mrc ON mprt.relieve_permissions_type = mrc.execute_event\n" +
            "WHERE\n" +
            "\tmprt.member_id = #{memberId}\n" +
            " and mrc.execute_event in (4,12) ORDER BY\n" +
            "\tmprt.relieve_time DESC\n" +
            "LIMIT 1")
    Date getLastForbiddenAdRelieveTime(@Param("memberId") Long memberId);
}