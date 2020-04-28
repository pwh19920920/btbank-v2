package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.mapper.MemberAssetStatisticsMapper;
import com.spark.bitrade.repository.entity.MemberAssetStatistics;
import com.spark.bitrade.repository.service.MemberAssetStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (MemberAssetStatistics)表服务实现类
 *
 * @author daring5920
 * @since 2019-12-24 11:50:00
 */
@Service("memberAssetStatisticsService")
public class MemberAssetStatisticsServiceImpl extends ServiceImpl<MemberAssetStatisticsMapper, MemberAssetStatistics> implements MemberAssetStatisticsService {

    @Autowired
    private MemberAssetStatisticsMapper memberAssetStatisticsMapper;
    @Override
    public List<MemberAssetStatistics> queryUserAsset(MemberAssetStatistics memberAssetStatistics) {
        return memberAssetStatisticsMapper.queryUserAsset(memberAssetStatistics);
    }

    @Override
    public int deleteAll() {
        return memberAssetStatisticsMapper.deleteAll();
    }

    @Override
    public int insertBath(List<MemberAssetStatistics> memberAssetStatisticslst) {
        return memberAssetStatisticsMapper.insertBath(memberAssetStatisticslst);
    }
}