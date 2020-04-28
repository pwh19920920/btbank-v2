package com.spark.bitrade.repository.service.impl;

import com.spark.bitrade.api.dto.OtcLimitAppealDTO;
import com.spark.bitrade.api.dto.OtcLimitDTO;
import com.spark.bitrade.repository.mapper.OtcLimitOperationsMapper;
import com.spark.bitrade.repository.service.OtcLimitOperationsService;
import com.spark.bitrade.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

/**
 * OtcLimitOperationsServiceImpl
 *
 * @author biu
 * @since 2019/11/28 17:24
 */
@Service
public class OtcLimitOperationsServiceImpl implements OtcLimitOperationsService {

    private OtcLimitOperationsMapper mapper;

    @Override
    public List<OtcLimitDTO> findLimitOtcOrder(Long memberId) {
        // 所有昨天之后的订单
        Calendar calendar = DateUtils.getCalendarOfYesterday();
        return mapper.findOtcLimitOrder(memberId, calendar.getTime());
    }

    @Override
    public List<OtcLimitAppealDTO> findLimitAppealByOrderIds(List<Long> ids) {
        return mapper.findLimitAppealByOrderIds(ids);
    }

    @Override
    public int balanceIsEnough(Long memberId, BigDecimal minimum) {
        return mapper.balanceIsEnough(memberId, minimum);
    }

    @Override
    public int countInFincMemberAccount(Long memberId) {
        return mapper.countInFincMemberAccount(memberId);
    }

    @Override
    public int countInPunishment(Long memberId, Integer type) {
        return mapper.countInPunishment(memberId, type);
    }

    @Autowired
    public void setMapper(OtcLimitOperationsMapper mapper) {
        this.mapper = mapper;
    }
}
