package com.spark.bitrade.api.controller;

import com.spark.bitrade.biz.ActivityRedpacketService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.ActivityRedPackReceiveRecord;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@Api(tags = {"红包升级控制器"})
@RequestMapping(path = "api/v2/advertise", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class ActivityRedpacketController {
    private ActivityRedpacketService activityRedpacketService;
    @ApiOperation(value = "获取赠送红包", response = ActivityRedPackReceiveRecord.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "红包类型 0挖矿赠送红包， 1推荐有效矿工赠送奖励", name = "triggerEvent", dataTypeClass = Integer.class)
    })
    @PostMapping(value = "/getMineRedPack")
    public MessageRespResult<ActivityRedPackReceiveRecord> getMineRedPack(@MemberAccount Member member, int triggerEvent) {
        ActivityRedPackReceiveRecord activityRedPackReceiveRecord = activityRedpacketService.getRedPack( member, triggerEvent);
        if(activityRedPackReceiveRecord==null){
            throw new BtBankException(BtBankMsgCode.NO_RED_PACKET_PIK);
        }
        return   MessageRespResult.success4Data(activityRedPackReceiveRecord);
    }
    @ApiOperation(value = "确认收到红包", response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "红包记录ID", name = "recordId", dataTypeClass = Long.class)
    })
    @PostMapping(value = "/ackMineRedPack")
    public MessageRespResult<Boolean> ackMineRedPack(Long recordId) {
        return   MessageRespResult.success4Data(activityRedpacketService.ackRedPack(recordId));
    }
}
