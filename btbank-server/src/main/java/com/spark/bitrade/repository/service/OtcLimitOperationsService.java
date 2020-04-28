package com.spark.bitrade.repository.service;

import com.spark.bitrade.api.dto.OtcLimitAppealDTO;
import com.spark.bitrade.api.dto.OtcLimitDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * OtcLimitOperationsService
 *
 * @author biu
 * @since 2019/11/28 17:23
 */
public interface OtcLimitOperationsService {

    List<OtcLimitDTO> findLimitOtcOrder(Long memberId);

    List<OtcLimitAppealDTO> findLimitAppealByOrderIds(List<Long> ids);

    int balanceIsEnough(Long memberId, BigDecimal minimum);

    int countInFincMemberAccount(Long memberId);

    int countInPunishment(Long memberId, Integer type);
}
