package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.vo.AdvertiseHistoryVo;
import com.spark.bitrade.biz.OtcConfigService;
import com.spark.bitrade.repository.entity.AdvertiseOperationHistory;
import com.spark.bitrade.repository.mapper.AdvertiseOperationHistoryMapper;
import com.spark.bitrade.repository.service.AdvertiseOperationHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 广告操作记录表(AdvertiseOperationHistory)表服务实现类
 *
 * @author daring5920
 * @since 2019-11-27 17:53:23
 */
@Slf4j
@Service("advertiseOperationHistoryService")
public class AdvertiseOperationHistoryServiceImpl extends ServiceImpl<AdvertiseOperationHistoryMapper, AdvertiseOperationHistory> implements AdvertiseOperationHistoryService {

    @Autowired
    OtcConfigService otcConfigService;

    @Override
    public List<Long> getMemberAdHistoryIds(Long memberId) {
        return baseMapper.getMemberAdHistoryIds(memberId);
    }


    @Override
    public List<AdvertiseOperationHistory> getAdHistroys(Long adId) {
        return baseMapper.getrAdHistroys(adId);
    }

    @Override
    public List<AdvertiseOperationHistory> listAdHistroysByMemberId(Long memberId,Date begin,Date end) {
        return baseMapper.listAdHistroysByMemberId(memberId,begin,end);
    }

    /**
     * * 商家连续3日不挂出售广告，则暂停1日商家出售广告资格；（3日是从下架广告开始连续计时），提示
     * * “由于连续3日未上架广告，禁止上架广告24小时”
     * * <p>
     */

    @Override
    public Boolean checkBusinessAdClosing(Long memberId) {

        AdvertiseOperationHistory history = this.lambdaQuery().eq(AdvertiseOperationHistory::getMemberId, memberId)
                .orderByDesc(AdvertiseOperationHistory::getCreateTime).last("limit 1").one();

        // 未发过广告的
        if (history == null) {
            return true;
        }

        log.info("checkBusinessAdClosing history {}", history);

        // 最后一个是上架广告
        if (history.getNewStatus() == 0) {
            return true;
        }


        if (history.getNewStatus() == 1) {

            // 排除禁止状态的规则计算
            Date relieveDate = baseMapper.getLastForbiddenAdRelieveTime(memberId);
            if(!Objects.isNull(relieveDate)){
                if(relieveDate.after(history.getCreateTime())) {
                    history.setCreateTime(relieveDate);
                }
            }
            long span = System.currentTimeMillis() - history.getCreateTime().getTime();
            double spanHour = span / 60.0 / 60.0 / 1000.0;
            log.info("checkBusinessAdClosing spanHour {} Hour ", spanHour);
            double reminder = spanHour % (otcConfigService.getOtcPausedBusinessAdOffHourSpan() + otcConfigService.getOtcPausedBusinessHoursWhenClosingLongTime());
            log.info("checkBusinessAdClosing reminder {} Hour ", reminder);
            if (reminder < otcConfigService.getOtcPausedBusinessAdOffHourSpan()) {
                return true;
            }
        }

        return false;
    }


}