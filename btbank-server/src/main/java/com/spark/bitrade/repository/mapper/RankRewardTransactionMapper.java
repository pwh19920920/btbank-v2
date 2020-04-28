package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.spark.bitrade.repository.entity.RankRewardTransaction;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 奖励金额流水(RankRewardTransaction)表数据库访问层
 *
 * @author daring5920
 * @since 2019-12-17 15:14:10
 */
@Mapper
public interface RankRewardTransactionMapper extends BaseMapper<RankRewardTransaction> {

    /**
     * 每日收益排行榜查询
     * @param dayTime
     * @return
     */
    List<RankRewardTransaction> getDayReward(@Param("dayTime") LocalDate dayTime);

    /**
     * 每日推广收益排行榜奖励
     * @param dayTime
     * @return
     */
    List<RankRewardTransaction> getDayExtensionReward(@Param("dayTime") LocalDate dayTime);

    /**
     * 累计收益排行榜奖励
     * @return
     */
    List<RankRewardTransaction> getTotalReward();

    /**
     * 查询当天奖励是否发放
     * @param memberId
     * @param dayTime
     * @param rewardType
     * @return
     */
    RankRewardTransaction findOne(@Param("memberId")Long memberId,@Param("dayTime")LocalDate dayTime,@Param("rewardType") int rewardType);

    List<RankRewardTransaction> getRankListByType(RankRewardTransaction rankRewardTransaction);
}