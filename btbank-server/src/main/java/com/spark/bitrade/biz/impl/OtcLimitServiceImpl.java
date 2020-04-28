package com.spark.bitrade.biz.impl;

import com.spark.bitrade.api.dto.OtcLimitAppealDTO;
import com.spark.bitrade.api.dto.OtcLimitDTO;
import com.spark.bitrade.biz.OtcLimitService;
import com.spark.bitrade.constant.MonitorExecuteEvent;
import com.spark.bitrade.repository.service.OtcLimitOperationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OtcLimitServiceImpl
 *
 * @author biu
 * @since 2019/11/28 16:20
 */
@Slf4j
@Service
public class OtcLimitServiceImpl implements OtcLimitService {

    private OtcLimitOperationsService otcLimitOperationsService;

    @Override
    public BigDecimal forbidToWithdrawAndTransferOut(Long memberId) {

        // > 10点之前 统计昨天0点之后的订单
        // > 10点到16点之间  统计昨天12点之后的订单
        // > 16点之后 统计今天的订单
        Date now = new Date();

        List<OtcLimitDTO> orders = otcLimitOperationsService.findLimitOtcOrder(memberId);

        if (orders == null || orders.size() == 0) {
            return BigDecimal.ZERO;
        }

        OtcLimitDTO.Range range = OtcLimitDTO.Range.of(now);
        // 申诉订单，额外处理
        List<OtcLimitDTO> appealList = new ArrayList<>();
        // 完成订单
        List<OtcLimitDTO> completeList = new ArrayList<>();

        for (OtcLimitDTO order : orders) {
            if (order.isValid(range)) {
                if (order.getState() == 3) {
                    completeList.add(order);
                } else {
                    appealList.add(order);
                }
            }
        }

        BigDecimal amount = BigDecimal.ZERO;

        // 查找所有胜诉的交易订单
        if (appealList.size() > 0) {
            List<Long> ids = appealList.stream().map(OtcLimitDTO::getId).collect(Collectors.toList());
            List<OtcLimitAppealDTO> dtos = otcLimitOperationsService.findLimitAppealByOrderIds(ids);
            Set<Long> winners = new HashSet<>();
            dtos.stream().filter(dto -> dto.isWinner(memberId)).forEach(dto -> winners.add(dto.getOrderId()));

            for (OtcLimitDTO dto : appealList) {
                if (winners.contains(dto.getId())) {
                    amount = amount.add(dto.getNumber());
                }
            }
        }

        // 正常完成的订单
        if (completeList.size() > 0) {
            for (OtcLimitDTO dto : completeList) {
                amount = amount.add(dto.getNumber());
            }
        }

        log.info("禁止转出的是{}",amount);
        return amount;
    }

    @Override
    public boolean balanceIsEnough(Long memberId, BigDecimal minimum) {
        return otcLimitOperationsService.balanceIsEnough(memberId, minimum) > 0;
    }


    @Override
    public boolean isInnerMerchant(Long memberId) {
        return otcLimitOperationsService.countInFincMemberAccount(memberId) > 0;
    }

    @Override
    public boolean isInPunishment(Long memberId) {
        return otcLimitOperationsService.countInPunishment(memberId, MonitorExecuteEvent.BT_WITHDRAW_SERVICE_FEE_RATE_UP.getOrdinal()) > 0;
    }

    @Autowired
    public void setOtcLimitOperationsService(OtcLimitOperationsService otcLimitOperationsService) {
        this.otcLimitOperationsService = otcLimitOperationsService;
    }

}
