package com.spark.bitrade.biz.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.spark.bitrade.api.vo.OtcWithdrawVO;
import com.spark.bitrade.biz.OtcConfigService;
import com.spark.bitrade.biz.OtcLimitService;
import com.spark.bitrade.biz.OtcMinerAutoService;
import com.spark.bitrade.biz.OtcMinerService;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.BusinessMinerOrder;
import com.spark.bitrade.repository.mapper.OtcMinerMapper;
import com.spark.bitrade.service.MemberWalletService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Slf4j
public class InnerMinerTest {
    @Autowired
    private OtcMinerMapper otcMinerMapper;
    @Autowired
    private MemberWalletService walletService;
    @Autowired
    private OtcConfigService otcConfigService;
    @Autowired
    private OtcLimitService otcLimitService;
    @Autowired
    private OtcMinerService otcMinerService;
    @Autowired
    private OtcMinerAutoService otcMinerAutoService;
    @Test
    public void testConfig(){
        // 获取提现服务费费率
        BigDecimal rate = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_SERVICE_RATE,
                BigDecimal::new,
                new BigDecimal("0.005"));
        // 获取时间限制
        String start = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_LIMIT_MIN,
                String::new,
                "00:00");
        String end = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_LIMIT_MAX,
                String::new,
                "00:00");
        System.out.println(rate);
        System.out.println(start);
        System.out.println(end);
    }
    @Test
    public void testInnerMiner(){
        //System.out.println(otcMinerMapper.chechInnerMember(360390L));

        WalletChangeRecord record = walletService.realseFreeze(TransactionType.OTC_WITHDRAW_FROZEN_CANCEL,280564L,"BT","BT",
                new BigDecimal(234),1L,"OTC提现系统取消");
        if (record == null) {
            log.error("系统取消otc取现订单失败 orderId = {}, member_id = {}, amount = {}");
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FAIL);
        }
        try {
            boolean b = walletService.confirmTrade(record.getMemberId(), record.getId());
            if (!b) {
                log.error("系统取消otc取现订单失败 record = {}", record);
                throw new BtBankException(CommonMsgCode.FAILURE);
            }else{
                log.info("系统取消otc取现订单成功");
            }
            throw new BtBankException(CommonMsgCode.FAILURE);
        } catch (RuntimeException ex) {
            log.error("系统取消otc取现订单失败 txId = {}, err = {}", record.getId(), ex.getMessage());
            boolean b = walletService.rollbackTrade(record.getMemberId(), record.getId());
            log.info("系统取消otc取现订单失败 result = {}, record = {}", b, record);
            throw ex;
        }
    }

    @Test
    public void TestDoubleRate(){
        // 获取提现服务费费率
       /* BigDecimal rate = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_SERVICE_RATE,
                BigDecimal::new,
                new BigDecimal("0.005"));
        // 获取时间限制
        String start = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_LIMIT_MIN,
                String::new,
                "00:00");
        String end = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_LIMIT_MAX,
                String::new,
                "00:00");
        if (otcLimitService.isInPunishment(360684L)) {
            rate = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_PUNISHMENT_SERVICE_RATE,
                    BigDecimal::new,
                    new BigDecimal("0.01"));
            log.info("处在风控规则处罚中, 使用处罚提现费率 rate = {}", rate);
        }
        //考虑到深夜商家休息，系统凌晨0：00分—早上8：00，提现手续费翻倍，已经被处罚的用户手续费2%，普通用户手续费1%；  2020-02-17 mahao
        LocalTime localTime = LocalTime.now();
        LocalTime min = LocalTime.parse(start);
        LocalTime max = LocalTime.parse(end);
        if (localTime.isAfter(min) && localTime.isBefore(max)) {
            BigDecimal rateTimes = otcConfigService.getValue(OtcConfigType.UPOTC_WITHDRAW_SERVICE_RATE,
                    BigDecimal::new,
                    new BigDecimal(2));
            System.out.println("rateTimes"+rateTimes);
            rate = rate.multiply(rateTimes);
            System.out.println("rateTimes,rate"+rate);
        }*/
        OtcWithdrawVO withdraw = otcMinerService.withdraw(360684L, new BigDecimal(108), PayMode.BANK);
        System.out.println(JSON.toJSONString(withdraw));

    }
    @Test
    public void testMemberRate(){
        String key = "OTC_WITHDRAW_SERVICE_RATE";
        if (OtcConfigType.OTC_WITHDRAW_SERVICE_RATE.equals(key)) {
            // 获取提现服务费费率
            BigDecimal rate = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_SERVICE_RATE,
                    BigDecimal::new,
                    new BigDecimal("0.005"));
            // 获取时间限制
            String start = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_LIMIT_MIN,
                    String::new,
                    "00:00");
            String end = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_LIMIT_MAX,
                    String::new,
                    "00:00");
            if (otcLimitService.isInPunishment(360684L)) {
                rate = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_PUNISHMENT_SERVICE_RATE,
                        BigDecimal::new,
                        new BigDecimal("0.01"));
                log.info("处在风控规则处罚中, 使用处罚提现费率 rate = {}", rate);
            }
            //考虑到深夜商家休息，系统凌晨0：00分—早上8：00，提现手续费翻倍，已经被处罚的用户手续费2%，普通用户手续费1%；  2020-02-17 mahao
            LocalTime localTime = LocalTime.now();
            LocalTime min = LocalTime.parse(start);
            LocalTime max = LocalTime.parse(end);
            if (localTime.isAfter(min) && localTime.isBefore(max)) {
                BigDecimal rateTimes = otcConfigService.getValue(OtcConfigType.UPOTC_WITHDRAW_SERVICE_RATE,
                        BigDecimal::new,
                        new BigDecimal(2));
                rate = rate.multiply(rateTimes);

            }

            System.out.println(rate);
        }
    }
    @Test
    public void test30Min(){
        otcMinerAutoService.autoProcessWithTimout30min();
    }
}
