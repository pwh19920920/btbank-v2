package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.EnterpriseMinerApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * 企业矿工申请表(EnterpriseMinerApplication)表数据库访问层
 *
 * @author biu
 * @since 2019-12-23 17:14:51
 */
@Mapper
@Repository
public interface EnterpriseMinerApplicationMapper extends BaseMapper<EnterpriseMinerApplication> {

    @Select("select * from enterprise_miner_application app where app.member_id = #{memberId} and app.`type` = 1 and app.`status` = 1 order by create_time desc limit 1")
    EnterpriseMinerApplication latestJoinApplication(@Param("memberId") Long memberId);

    @Select("select * from enterprise_miner_application app where app.member_id = #{memberId} order by create_time desc limit 1")
    EnterpriseMinerApplication latestApplication(@Param("memberId") Long memberId);
}