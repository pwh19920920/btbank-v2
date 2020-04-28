package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.spark.bitrade.repository.entity.MemberAssetStatistics;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (MemberAssetStatistics)表数据库访问层
 *
 * @author daring5920
 * @since 2019-12-24 11:50:00
 */
@Mapper
public interface MemberAssetStatisticsMapper extends BaseMapper<MemberAssetStatistics> {

    List<MemberAssetStatistics> queryUserAsset(MemberAssetStatistics memberAssetStatistics);
    int deleteAll();
    int insertBath(@Param("memberAssetStatistics")  List<MemberAssetStatistics> memberAssetStatistics);
}