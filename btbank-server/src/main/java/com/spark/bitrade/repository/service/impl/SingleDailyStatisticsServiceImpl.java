package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.SingleDailyStatistics;
import com.spark.bitrade.repository.mapper.SingleDailyStatisticsMapper;
import com.spark.bitrade.repository.service.SingleDailyStatisticsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (SingleDailyStatistics)表服务实现类
 *
 * @author daring5920
 * @since 2020-03-23 13:42:21
 */
@Service("singleDailyStatisticsService")
public class SingleDailyStatisticsServiceImpl extends ServiceImpl<SingleDailyStatisticsMapper, SingleDailyStatistics> implements SingleDailyStatisticsService {

    @Override
    public List<SingleDailyStatistics> recordOTCBuyRank(String startTime, String endTime) {
        return baseMapper.buyOTCRank(startTime, endTime);
    }

    @Override
    public List<SingleDailyStatistics> recordOTCSellRank(String startTime, String endTime) {
        return baseMapper.sellOTCRank(startTime, endTime);
    }

    @Override
    public List<SingleDailyStatistics> findPayFastReceiveRank(String startTime, String endTime) {
        return getBaseMapper().findPayFastReceiveRank(startTime, endTime);
    }

    @Override
    public List<SingleDailyStatistics> findPayFastPayRank(String startTime, String endTime) {
        return getBaseMapper().findPayFastPayRank(startTime, endTime);
    }
}