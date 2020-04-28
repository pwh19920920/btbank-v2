package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.SingleDealStatistics;
import com.spark.bitrade.repository.mapper.SingleDealStatisticsMapper;
import com.spark.bitrade.repository.service.SingleDealStatisticsService;
import com.spark.bitrade.util.DateUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 单个交易日OTC统计(RankRewardTransaction)表数据库访问层
 *
 * @author qiuyuanjie
 * @since 2020-03-25 15:14:10
 */
@Service("singleDealStatisticsService")
public class SingleDealStatisticsServiceImpl extends ServiceImpl<SingleDealStatisticsMapper,SingleDealStatistics> implements SingleDealStatisticsService {

    @Override
    public SingleDealStatistics staSingleTransactionOTC(String startTime,String endTime) {
        SingleDealStatistics buy = baseMapper.statisticsOtcBuy(startTime,endTime);
        SingleDealStatistics sell = baseMapper.statisticsOtcSell(startTime,endTime);
        if (buy == null && sell == null){
            return null;
        }
        if (buy != null && sell == null){
            buy.setCurrentSellTotal(BigDecimal.ZERO);
            return buy;
        }else if (sell != null && buy == null){
            sell.setCurrentBuyTotal(BigDecimal.ZERO);
            return sell;
        }else {
            buy.setCurrentSellTotal(sell.getCurrentSellTotal());
            return buy;
        }
    }
}
