package com.spark.bitrade.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.api.vo.AdvertiseHistoryVo;
import com.spark.bitrade.biz.AdvertiseService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.AdvertiseOperationHistory;
import com.spark.bitrade.repository.entity.ForeignOfflineExchange;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Date;

/**
 * @author ww
 * @time 2019.11.28 09:16
 */
@Slf4j
@Api(tags = {"广告操作日志"})
@RequestMapping(path = "api/v2/advertise", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class AdvertiseController {

    @Autowired
    AdvertiseService advertiseService;

    @ApiOperation(value = "保存日志", response = Boolean.class)
    @PostMapping(value = "saveOperationHistory")
    public MessageRespResult<Boolean> saveOperationHistory(Long adId, int oldStatus, int newStatus, int advertiseType, long memberId) {
        AdvertiseOperationHistory history = new AdvertiseOperationHistory();
        history.setId(IdWorker.getId());
        history.setAdvertiseId(adId);
        history.setCreateTime(new Date());
        history.setUpdateTime(new Date());
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setAdvertiseType(advertiseType);
        history.setMemberId(memberId);
        return MessageRespResult.success4Data(advertiseService.saveHistory(history));
    }
    @ApiOperation(value = "广告操作日志分页", response = AdvertiseHistoryVo.class)
    @PostMapping(value = "getAdvertiseHistory")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "广告ID", name = "id", dataTypeClass = Long.class),
            @ApiImplicitParam(value = "当前页", name = "current", dataTypeClass = Integer.class),
            @ApiImplicitParam(value = "每页条数", name = "size", dataTypeClass = Integer.class)
    })
    public MessageRespResult< IPage<AdvertiseHistoryVo> > getAdvertiseHistory(@MemberAccount Member member,Long id, Integer current,Integer size) {
        if(current==null){
            current = 1;
        }
        if(size==null){
            size = 10;
        }
        IPage<AdvertiseHistoryVo> page = advertiseService.getAdvertiseHistory(member, id, current, size);
        return MessageRespResult.success4Data(page);
    }


    @ApiOperation(value = "广告累计时长操作", response = AdvertiseHistoryVo.class)
    @PostMapping(value = "getCumulativeTime")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "开始时间", name = "startTime", dataTypeClass = String.class),
            @ApiImplicitParam(value = "结束时间", name = "endTime", dataTypeClass = String.class)
    })
    public MessageRespResult<String> getCumulativeTime(@MemberAccount Member member, String startTime, String endTime) {
        String time = "0";
        try {
            time = advertiseService.findCumulativeTime(member.getId(), startTime, endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return MessageRespResult.success4Data(time);
    }

}
