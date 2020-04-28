package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spark.bitrade.api.dto.WelfareRewardStateDto;
import com.spark.bitrade.biz.WelfareReleaseService;
import com.spark.bitrade.biz.WelfareService;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.constant.WelfareDateDef;
import com.spark.bitrade.repository.entity.WelfareActivity;
import com.spark.bitrade.repository.entity.WelfareInvolvement;
import com.spark.bitrade.repository.mapper.WelfareMapper;
import com.spark.bitrade.repository.service.WelfareActivityService;
import com.spark.bitrade.repository.service.WelfareInvolvementService;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.trans.Tuple2;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WelfareServiceImpl
 *
 * @author biu
 * @since 2020/4/9 16:40
 */
@Slf4j
@Service
@AllArgsConstructor
public class WelfareServiceImpl implements WelfareService {

    private final WelfareMapper welfareMapper;
    private final WelfareActivityService activityService;
    private final WelfareInvolvementService involvementService;
    private final WelfareReleaseService releaseService;
    private final BtBankConfigService configService;

    @Override
    public boolean checkWalletBalance(Long memberId, BigDecimal amount) {
        return welfareMapper.checkWalletBalance(memberId, amount) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean isAvailableMiner(Long memberId) {
        return welfareMapper.isAvailableMiner(memberId) > 0;
    }

    @Override
    public boolean autoCreateWelfarePacket() {
        // 时间
        Date openningTime = WelfareDateDef.getOpenningTime();
        Date closingTime = WelfareDateDef.getClosingTime();
        Date releaseTime = WelfareDateDef.getReleaseTime(closingTime);

        // 检查是否重复
        Tuple2<Date, Date> timeRange = WelfareDateDef.getTimeRange(openningTime);
        Integer newPack = activityService.lambdaQuery().eq(WelfareActivity::getType, 0)
                .gt(WelfareActivity::getOpenningTime, timeRange.getFirst())
                .lt(WelfareActivity::getClosingTime, timeRange.getSecond()).count();

        Integer incrPack = activityService.lambdaQuery().eq(WelfareActivity::getType, 1)
                .gt(WelfareActivity::getOpenningTime, timeRange.getFirst())
                .lt(WelfareActivity::getClosingTime, timeRange.getSecond()).count();

        List<WelfareActivity> list = new ArrayList<>();
        if (newPack == 0) {
            list.add(build(0, openningTime, closingTime, releaseTime));
        }
        Integer incrOnline = configService.getConfig("WELFARE_INCR_PACKET_ONLINE", r -> Integer.parseInt(r.toString()), 0);
        if (incrPack == 0 && new Integer(1).equals(incrOnline)) {
            list.add(build(1, openningTime, closingTime, releaseTime));
        }

        if (list.size() > 0) {
            return activityService.saveBatch(list);
        }
        return false;
    }

    @Override
    public void release() {
        Date now = Calendar.getInstance().getTime();
        // 获取该释放的
        LambdaQueryWrapper<WelfareInvolvement> query = new LambdaQueryWrapper<WelfareInvolvement>()
                .eq(WelfareInvolvement::getStatus, 0)
                .lt(WelfareInvolvement::getReleaseTime, now)
                .lt(WelfareInvolvement::getReleaseStatus, 4); // 释放流程未完全结束的

        involvementService.list(query).parallelStream().forEach(item -> {
            try {
                // 释放本金
                releaseService.principal(item);
                // 释放利息
                releaseService.interest(item);
                // 释放直推
                releaseService.invite(item);
                // 释放金牌
                releaseService.gold(item);
            } catch (RuntimeException ex) {
                log.error("释放福利包收益失败 [member_id = {}, involvement_id = {}, err = '{}' ]",
                        item.getMemberId(), item.getId(), ex.getMessage());
            }
        });
    }

    @Override
    public void checkRewardStatus() {
        List<Long> ids = welfareMapper.findUnReceivedRewardRecords().stream()
                .filter(r -> 1 == r.getStatus()).map(WelfareRewardStateDto::getId).collect(Collectors.toList());
        if (ids.size() > 0) {
            Date now = Calendar.getInstance().getTime();
            boolean update = involvementService.lambdaUpdate().in(WelfareInvolvement::getId, ids)
                    .eq(WelfareInvolvement::getRecommendStatus, 1)
                    .set(WelfareInvolvement::getRecommendStatus, 2)
                    .set(WelfareInvolvement::getUpdateTime, now).update();
            log.info("同步福利挖矿直推领取状态 [ result = {}, ids = {} ]", update, ids);
        } else {
            log.info("同步福利挖矿直推领取状态 无需要同步数据");
        }

    }

    private WelfareActivity build(int type, Date openning, Date closing, Date release) {
        Integer period = welfareMapper.getMaxPeriod(type);

        WelfareActivity activity = new WelfareActivity();
        activity.setName(String.format(type == 0 ? "新人福利挖矿第%s期活动" : "增值福利挖矿第%s期活动", numberToChinese(period)));
        activity.setType(type);
        activity.setPeriod(period);
        // 固定 activity.setLockTime();
        activity.setEarningRate(BigDecimal.valueOf(0.075)); // fixed
        activity.setOpenningTime(openning);
        activity.setClosingTime(closing);
        activity.setReleaseTime(release);
        activity.setAmount(BigDecimal.valueOf(10000)); // fixed

        Integer config = configService.getConfig(BtBankSystemConfig.WELFARE_NEW_EXPIRED_DAYS, r -> Integer.parseInt(r.toString()), 21);
        activity.setRemark("新人福利包，新会员注册之日起" + config + "日内可获得1次购买资格；每直接推荐一名新会员购买福利包，推荐人可获得一次福利包购买资格");
        if (type == 1) {
            activity.setRemark("增值福利包，仅限参加过增值计划的会员购买");
        }
        activity.setCreateTime(Calendar.getInstance().getTime());
        return activity;
    }

    private String numberToChinese(int number) {
        String[] numbers = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] units = {"", "十", "百", "千", "万", "十", "百", "千", "亿", "十"};
        String sign = number < 0 ? "负" : "";
        if (number < 0) {
            number = -number;
        }
        StringBuilder result = new StringBuilder(sign);
        String string = String.valueOf(number);
        int n = string.length();
        char[] numberCharArray = string.toCharArray();
        for (int i = 0; i < n; i++) {
            int digNum = n - i; // 位数
            int num = numberCharArray[i] - '0';
            if (num != 0) {
                result.append(numbers[num]).append(units[digNum - 1]);
                continue;
            }

            if (result.toString().endsWith(numbers[0])) {
                // 如果是单位所在的位数，则去除上一个0，加上单位
                if (digNum % 4 == 1) {
                    result.deleteCharAt(result.length() - 1);
                    result.append(units[digNum - 1]);
                }
            } else {
                result.append(numbers[0]);
            }
        }
        // 处理 一十零 -> 十
        int length = result.length();
        if (String.valueOf(result.charAt(length - 1)).equals("零")) {
            result.deleteCharAt(length - 1);
        }
        if (length > 1 && String.valueOf(result.charAt(0)).equals("一") && String.valueOf(result.charAt(1)).equals("十")) {
            result.deleteCharAt(0);
        }
        return result.toString();
    }
}
