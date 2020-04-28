package com.spark.bitrade.api.controller;

import com.spark.bitrade.api.vo.OtcConfigVO;
import com.spark.bitrade.biz.OtcConfigService;
import com.spark.bitrade.biz.OtcLimitService;
import com.spark.bitrade.constant.OtcConfigType;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Map;

/**
 * @author ww
 * @time 2019.11.28 10:25
 */
@Slf4j
@Api(tags = {"OTC配置"})
@RequestMapping(path = "api/v2/otcConfig", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class OtcConfigController {

    private OtcConfigService configService;
    private OtcLimitService otcLimitService;

    @ApiOperation(value = "获取指定OTC配置接口", response = MessageRespResult.class)
    @PostMapping(value = "getValue")
    public MessageRespResult<String> getValue(@MemberAccount Member member, String key) {

        if (OtcConfigType.OTC_WITHDRAW_SERVICE_RATE.equals(key)) {
            // 获取提现服务费费率
            BigDecimal rate = configService.getValue(OtcConfigType.OTC_WITHDRAW_SERVICE_RATE,
                    BigDecimal::new,
                    new BigDecimal("0.005"));
            // 获取时间限制
            String start = configService.getValue(OtcConfigType.OTC_WITHDRAW_LIMIT_MIN,
                    String::new,
                    "00:00");
            String end = configService.getValue(OtcConfigType.OTC_WITHDRAW_LIMIT_MAX,
                    String::new,
                    "00:00");
            if (otcLimitService.isInPunishment(member.getId())) {
                rate = configService.getValue(OtcConfigType.OTC_WITHDRAW_PUNISHMENT_SERVICE_RATE,
                        BigDecimal::new,
                        new BigDecimal("0.01"));
                log.info("处在风控规则处罚中, 使用处罚提现费率 rate = {}", rate);
            }
            //考虑到深夜商家休息，系统凌晨0：00分—早上8：00，提现手续费翻倍，已经被处罚的用户手续费2%，普通用户手续费1%；  2020-02-17 mahao
            LocalTime localTime = LocalTime.now();
            LocalTime min = LocalTime.parse(start);
            LocalTime max = LocalTime.parse(end);
            if (localTime.isAfter(min) && localTime.isBefore(max)) {
                BigDecimal rateTimes = configService.getValue(OtcConfigType.UPOTC_WITHDRAW_SERVICE_RATE,
                        BigDecimal::new,
                        new BigDecimal(2));
                rate = rate.multiply(rateTimes);

            }
            return MessageRespResult.success4Data(rate.toString());

        }
        return MessageRespResult.success4Data(configService.getValue(key));
    }


    @ApiOperation(value = "获取OTC配置接口", response = OtcConfigVO.class)
    @PostMapping(value = "getSetting")
    public MessageRespResult<Map<String, String>> getOtcSetting() {

        OtcConfigVO configVO = new OtcConfigVO();
        configVO.setAutoPullOffReminBalanceLimit(configService.getValue(OtcConfigType.OTC_AD_AUTO_DOWN_BALANCE));
        configVO.setBusinessSaleRewardRate(configService.getValue(OtcConfigType.OTC_BUSINESS_SALE_REWARD_RATE));
        return MessageRespResult.getSuccessInstance("suceess ", configVO);
    }

}
