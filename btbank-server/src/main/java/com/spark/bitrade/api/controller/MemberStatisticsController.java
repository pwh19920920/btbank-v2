package com.spark.bitrade.api.controller;

import com.spark.bitrade.repository.entity.BtBankGeneralStatement;
import com.spark.bitrade.repository.entity.MemberAssetStatistics;
import com.spark.bitrade.repository.service.MemberAssetStatisticsService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.SpringContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by mahao on 2019/12/24.
 */
@Slf4j
@Api(tags = {"用户理财报表统计"})
@RequestMapping(path = "memberStatistics/report", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class MemberStatisticsController {
    @Autowired
    private MemberAssetStatisticsService memberAssetStatisticsService;

    @ApiOperation(value = "用户理财表统计查询", response = BtBankGeneralStatement.class)
    @PostMapping(value = "memberStatistics")
    public MessageRespResult<String> generalMemberStatistics() {
        SpringContextUtil.getBean(MemberStatisticsController.class).memberStatistics();
        return MessageRespResult.success();
    }

    @Async
    public void memberStatistics(){
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime startTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endTime =  LocalDateTime.now();
        MemberAssetStatistics memberAssetStatistics = new MemberAssetStatistics();
        ZonedDateTime startDate = startTime.atZone(zoneId);
        ZonedDateTime endDate = endTime.atZone(zoneId);
        memberAssetStatistics.setStartTime( Date.from(startDate.toInstant()));
        memberAssetStatistics.setEndTime(Date.from(endDate.toInstant()));
        List<MemberAssetStatistics> lst = memberAssetStatisticsService.queryUserAsset(memberAssetStatistics);
        if(lst.size()>0){
            memberAssetStatisticsService.deleteAll();
            memberAssetStatisticsService.saveBatch(lst);
        }
    }
}
