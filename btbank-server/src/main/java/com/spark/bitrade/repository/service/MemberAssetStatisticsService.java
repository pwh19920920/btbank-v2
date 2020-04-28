package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.MemberAssetStatistics;

import java.util.List;

/**
 * (MemberAssetStatistics)表服务接口
 *
 * @author daring5920
 * @since 2019-12-24 11:50:00
 */
public interface MemberAssetStatisticsService extends IService<MemberAssetStatistics> {

    List<MemberAssetStatistics> queryUserAsset(MemberAssetStatistics memberAssetStatistics);
    int deleteAll();
    int insertBath(List<MemberAssetStatistics> memberAssetStatistics);

}