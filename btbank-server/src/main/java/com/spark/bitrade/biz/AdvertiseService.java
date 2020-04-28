package com.spark.bitrade.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.api.vo.AdvertiseHistoryVo;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.AdvertiseOperationHistory;
import com.spark.bitrade.util.MessageRespResult;

import java.text.ParseException;

/**
 * @author ww
 * @time 2019.11.28 09:21
 */
public interface AdvertiseService {

    /**
     * 保存一个广告操作历史
     *
     * @param history
     */
    boolean saveHistory(AdvertiseOperationHistory history);

    /**
     * 对已经出售的订单进行补贴
     */
    MessageRespResult dispatchOtcSaleReward();

    /**
     * 对已商家按天进行补贴
     */
    MessageRespResult dispatchOtcSaleRewardForDay();

    IPage<AdvertiseHistoryVo> getAdvertiseHistory(Member member, Long id, Integer current, Integer size);

    String findCumulativeTime(Long memberId, String startTime, String endTime) throws ParseException;
}
