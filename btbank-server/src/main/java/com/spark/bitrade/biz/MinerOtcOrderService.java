package com.spark.bitrade.biz;

import com.spark.bitrade.repository.entity.OtcOrder;

import java.util.Date;
import java.util.List;

/**
 * @author ww
 * @time 2019.11.29 10:41
 */
public interface MinerOtcOrderService {
    void dispatchSaleReward();

    List<Date> getNeedDispatchDateList();

    List<Long> getNeedDispatchMemberIds();

    Date getEarliestDate();

    void dispatchSaleRewardForDay(Date beginTime, Date endTime, Long memberId);

    void dispatchSaleOrderReward(OtcOrder order);
}
