package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.api.vo.AdvertiseHistoryVo;
import com.spark.bitrade.biz.AdvertiseService;
import com.spark.bitrade.biz.MinerOtcOrderService;
import com.spark.bitrade.biz.OtcConfigService;
import com.spark.bitrade.constant.OtcConfigType;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.AdvertiseOperationHistory;
import com.spark.bitrade.repository.service.AdvertiseOperationHistoryService;
import com.spark.bitrade.util.BigDecimalUtils;
import com.spark.bitrade.util.DateUtils;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * @author ww
 * @time 2019.11.28 09:21
 */
@Slf4j
@Service
public class AdvertiseServiceImpl implements AdvertiseService {
    @Autowired
    AdvertiseOperationHistoryService historyService;

    @Autowired
    MinerOtcOrderService otcOrderService;

    @Autowired
    OtcConfigService configService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveHistory(AdvertiseOperationHistory history) {
        return historyService.save(history);
    }

    @Override
    public MessageRespResult dispatchOtcSaleReward() {

        //从订单表查询需要发放的订单所在的日期
        // List<Date> needDispatchDateList = otcOrderService.getNeedDispatchDateList();

        //Date getEarliestDate = otcOrderService.getEarliestDate();


        //log.info("需要发放商家销售补贴的日期 {}", needDispatchDateList);

        //发所有收益
        otcOrderService.dispatchSaleReward();


        return MessageRespResult.success();

    }

    @Override
    public MessageRespResult dispatchOtcSaleRewardForDay() {


        log.info(" 发放收益不足补贴");

        String payMemberIdString = configService.getValue(OtcConfigType.OTC_SALE_REWARD_PAY_ACCOUNT);
        Long payMemberId = Long.valueOf(payMemberIdString);

        String excludeIdsString = configService.getValue(OtcConfigType.OTC_SALE_REWARD_SUBSIDY_ACCOUNT_EXCLUDE);
        List<Long> excludeMemeberIds = new ArrayList<>();
        excludeMemeberIds.add(payMemberId);
        Arrays.stream(excludeIdsString.split(",")).forEach(o ->
                excludeMemeberIds.add(Long.valueOf(o)));



        Calendar endCal = Calendar.getInstance();
        endCal.setTime(new Date());
        endCal.set(Calendar.MILLISECOND, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.HOUR_OF_DAY, 16);

        // if (endCal.getTime().compareTo(new Date()) > 0) {
        //     endCal.add(Calendar.DATE, -1);
        // }
        Calendar beginCal = Calendar.getInstance();
        beginCal.setTime(endCal.getTime());
        beginCal.add(Calendar.DATE, -1);


        List<Long> needDispatchMemberIds = otcOrderService.getNeedDispatchMemberIds();

        for (Long memberId : needDispatchMemberIds) {
            //获取统计然后进行补贴 给指定 的用户在指定 的日期发放

            if (excludeMemeberIds.contains(memberId)) {
                log.info(" 支付专用号和排除帐号不需要发放 {}", memberId);
            } else {
                otcOrderService.dispatchSaleRewardForDay(beginCal.getTime(), endCal.getTime(), memberId);
            }
        }
        //
        return MessageRespResult.success();
    }

    @Override
    public IPage<AdvertiseHistoryVo> getAdvertiseHistory(Member member,Long id, Integer current, Integer size) {
        IPage<AdvertiseOperationHistory> page = new Page<>(current, size);
        QueryWrapper<AdvertiseOperationHistory> query = new QueryWrapper<>();
        query.lambda().eq(AdvertiseOperationHistory::getMemberId,member.getId()).orderByDesc(AdvertiseOperationHistory::getCreateTime);
        if(id!=null&&id>0){
            query.lambda().eq(AdvertiseOperationHistory::getAdvertiseId,id);
        }
        IPage<AdvertiseHistoryVo> result = historyService.page(page, query).convert(AdvertiseHistoryVo::of);
        return result;
    }

    @Override
    public String findCumulativeTime(Long memberId, String startTime, String endTime) throws ParseException {
        long onlineMinutes = 0;
        List<AdvertiseOperationHistory> historiesAll = new ArrayList<>();
        List<AdvertiseOperationHistory> histories = historyService.listAdHistroysByMemberId(memberId, DateUtils.stringToDate(startTime), DateUtils.stringToDate(endTime));
        if (histories == null || histories.size() < 1) {
            AdvertiseOperationHistory advertiseOperationHistory = historyService.lambdaQuery()
                    .eq(AdvertiseOperationHistory::getMemberId, memberId)
                    .le(AdvertiseOperationHistory::getCreateTime, startTime)
                    .orderByDesc(AdvertiseOperationHistory::getCreateTime).last(" limit 1").one();
            if (Objects.isNull(advertiseOperationHistory)) {
                return "0";
            }
            if (advertiseOperationHistory.getNewStatus() == 1) {
                return "0";
            }
            histories = new ArrayList<>();
            if (new Date().compareTo(DateUtils.stringToDate(endTime)) < 1) {
                advertiseOperationHistory.setCreateTime(new Date());
            } else {
                advertiseOperationHistory.setCreateTime(DateUtils.stringToDate(startTime));
            }
            advertiseOperationHistory.setCreateTime(DateUtils.stringToDate(startTime));
            histories.add(advertiseOperationHistory);
        }

        if (histories.get(0).getNewStatus() == 1) {
            AdvertiseOperationHistory advertiseOperationHistory = new AdvertiseOperationHistory();
            advertiseOperationHistory.setOldStatus(1);
            advertiseOperationHistory.setNewStatus(0);
            advertiseOperationHistory.setCreateTime(DateUtils.stringToDate(startTime));
            historiesAll.add(advertiseOperationHistory);
        }
        historiesAll.addAll(histories);
        if (historiesAll.get(historiesAll.size() - 1).getNewStatus() == 0) {
            AdvertiseOperationHistory advertiseOperationHistory = new AdvertiseOperationHistory();
            advertiseOperationHistory.setOldStatus(0);
            advertiseOperationHistory.setNewStatus(1);
            if (new Date().compareTo(DateUtils.stringToDate(endTime)) < 1 && historiesAll.size() == 1 && historiesAll.get(historiesAll.size() - 1).getUpdateTime().compareTo(DateUtils.stringToDate(startTime)) < 1) {
                historiesAll.get(historiesAll.size() - 1).setCreateTime(DateUtils.stringToDate(startTime));
                advertiseOperationHistory.setCreateTime(new Date());
            } else if (new Date().compareTo(DateUtils.stringToDate(endTime)) < 1){
                advertiseOperationHistory.setCreateTime(new Date());
            } else {
                advertiseOperationHistory.setCreateTime(DateUtils.stringToDate(endTime));
            }
            historiesAll.add(advertiseOperationHistory);
        }

        for (int i = 0; i < historiesAll.size() - 1; i++) {
            AdvertiseOperationHistory lastHistory = historiesAll.get(i);
            AdvertiseOperationHistory nextHistory = historiesAll.get(i + 1);
            if (lastHistory.getNewStatus() == 0 && nextHistory.getNewStatus() == 1) {
                onlineMinutes = onlineMinutes + BigDecimalUtils.div(
                        new BigDecimal(nextHistory.getCreateTime().getTime() - lastHistory.getCreateTime().getTime()),
                        new BigDecimal("60000")).longValue();
            }
        }

        if (onlineMinutes > 0) {
            log.info("用户  {}  广告总在线时长 {} 分钟", memberId, onlineMinutes);
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(1);
            nf.setRoundingMode(RoundingMode.DOWN);
            String time = nf.format((double) onlineMinutes / 60);
            return time;
        }
        return "0";
    }
}
