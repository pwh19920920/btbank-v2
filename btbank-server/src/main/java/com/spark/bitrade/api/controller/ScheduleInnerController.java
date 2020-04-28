package com.spark.bitrade.api.controller;

import com.spark.bitrade.biz.*;
import com.spark.bitrade.util.DateUtils;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author shenzucai
 * @time 2019.10.24 16:32
 */
@Api(tags = "定时调度控制器，仅限内网调用")
@RequestMapping(value = "inner/schedule", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
@Slf4j
public class ScheduleInnerController {

    private AdvertiseService advertiseService;
    private ScheduleService scheduleService;
    private MinerRebateService minerRebateService;
    private MinerRebateCheckService minerRebateCheckService;
    private OtcMinerAutoService otcMinerAutoService;
    private MinerOrderReportService reportService;
    private OtcMinerService otcMinerService;
    private MemberExperienceBizService memberExperienceBizService;
    @ApiOperation(value = "自动派单")
    @PostMapping("auto/dispatch")
    public MessageRespResult autoDispatch() {
        scheduleService.autoDispatch();
        return MessageRespResult.success();
    }

    @ApiOperation(value = "自动解锁")
    @PostMapping("auto/unlock")
    public MessageRespResult autoUnlock() {
        scheduleService.unLockAssert();
        return MessageRespResult.success();
    }

    @ApiOperation(value = "自动处理超时矿池订单")
    @PostMapping(value = "auto/process")
    public MessageRespResult<String> autoProcessOrderWithTimeout30Minutes() {
        return MessageRespResult.success4Data(otcMinerAutoService.autoProcessWithTimout30min());
    }

    @ApiOperation(value = "处理矿工返利")
    @PostMapping("miner/rebate")
    public MessageRespResult minerRebate() {
         minerRebateService.processRebate();
        return MessageRespResult.success();
    }


    @ApiOperation(value = "生成订单统计报告")
    @PostMapping("miner/orderStatisticalReport")
    public MessageRespResult orderStatisticalReport(@RequestParam(defaultValue = "", required = false) String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = DateUtils.getCalendarOfYesterday(); //默认第二天00点以后计算等明天的
        Date queryDate = cal.getTime();

        try {
            if (!StringUtils.isEmpty(date)) {
                queryDate = sdf.parse(date);
            }
            reportService.orderStatisticalReport(queryDate);
        } catch (ParseException e) {
            MessageRespResult.error(e.getMessage());
        }

        return MessageRespResult.success();
    }


    /**
     * 派发销售补贴
     *
     * @return
     */

    @ApiOperation(value = "商家销售补贴")
    @PostMapping(value = "miner/dispatchOtcSaleReward")
    public MessageRespResult dispatchOtcSaleReward() {
        return advertiseService.dispatchOtcSaleReward();
    }


    @ApiOperation(value = "商家销售按天补贴")
    @PostMapping(value = "miner/dispatchOtcSaleRewardForDay")
    public MessageRespResult dispatchOtcSaleRewardForDay() {
        return advertiseService.dispatchOtcSaleRewardForDay();
    }

    @ApiOperation(value = "检查并重做奖励")
    @ApiImplicitParam(value = "开始时间; 不填写默认昨日0点开始, 格式: yyyy-MM-dd HH:mm:ss", name = "begin")
    @PostMapping("/miner/checkRebate")
    public MessageRespResult checkRebate(@RequestParam(value = "begin", required = false) String begin) {

        Date date = null;
        if (org.springframework.util.StringUtils.hasText(begin)) {
            date = DateUtils.parseDatetime(begin);
        }

        if (date == null) {
            Calendar yesterday = DateUtils.getCalendarOfYesterday();
            date = yesterday.getTime();
        }

        minerRebateCheckService.checkRebate(date);
        return MessageRespResult.success();
    }


    @ApiOperation(value = "理财活动状态监测")
    @PostMapping("auto/updateActivity")
    public MessageRespResult autoUpdateActivity() {
        scheduleService.autoUpdateActivity();
        return MessageRespResult.success();
    }

    @ApiOperation(value = "理财活动收益及解锁")
    @PostMapping("auto/profitUnlock")
    public MessageRespResult autoProfitUnlock() {
        scheduleService.autoProfitUnlock();
        return MessageRespResult.success();
    }

    @ApiOperation(value = "理财活动直推奖励")
    @PostMapping("auto/recommendUnlock")
    public MessageRespResult recommendUnlock() {
        scheduleService.recommendUnlock();
        return MessageRespResult.success();
    }

    @ApiOperation(value = "kafka异常修复异常订单问题")
    @PostMapping("auto/recoveryOTCOrderStatus")
    public MessageRespResult recoveryOTCOrderStatus() {

        otcMinerService.updateOtcOrderStaus();

        return MessageRespResult.success();
    }


    @ApiOperation(value = "3月8日 新矿工累计收益到600BT释放体验金")
    @PostMapping("auto/releaseProfit")
    public MessageRespResult autoReleaseProfit() {
        scheduleService.autoReleaseProfit();
        memberExperienceBizService.lockExperience();
        return MessageRespResult.success();
    }


    @ApiOperation(value = "3月8日 老矿工推荐福利")
    @PostMapping("auto/oldMemberRelease")
    public MessageRespResult oldMemberRelease() {
        memberExperienceBizService.oldMemberRelease();
        return MessageRespResult.success();
    }
}
