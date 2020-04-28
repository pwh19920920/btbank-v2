package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.api.dto.FixOrderDto;
import com.spark.bitrade.repository.entity.OtcOrder;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * (OtcOrder)表服务接口
 *
 * @author daring5920
 * @since 2019-11-28 17:43:20
 */
public interface OtcOrderService extends IService<OtcOrder> {

    List<OtcOrder> getNeedDispatchOrdersOrder();

    List<Date> getNeedDispatchDateList();

    List<Long> getNeedDispatchMemberIds();

    BigDecimal getTotalSaleReward(Date beginTime, Date endTime, Long memberId);

    int updateOrderDispatchSaleRewardStatus(OtcOrder order);

    Date getEarliestDate();

    Optional<OtcOrder> findOneByOrderSn(Long orderSn);

    int countOrders(Long memberId, boolean isBuy);

    int queryOtcorderCnt();

    List<FixOrderDto> queryFixOrders();

    int updateOtcOrderStaus();

    Boolean updateFixOtcOrderStaus(@Param("fixOrderDto") FixOrderDto fixOrderDto);
}