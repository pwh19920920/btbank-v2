package com.spark.bitrade.api.controller;

import com.spark.bitrade.biz.ActivityRedpacketService;
import com.spark.bitrade.repository.entity.ActivityRedPackManage;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(tags = {"系统发放红包账户释放锁仓到可用"})
@RequestMapping(path = "inner/sheduleActivityRedPacket", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class SheduleActivityRedPacketController {

    private ActivityRedpacketService activityRedpacketService;
    @ApiOperation(value = "活动结束释放锁仓金额")
    @PostMapping(value = "realseLockAmount")
    public MessageRespResult<String> realseLockAmount() {
        MessageRespResult<String> result = new MessageRespResult();
        List<ActivityRedPackManage> list = activityRedpacketService.getRealseLockAmountActivity();
        if(list.size()==0){
            log.info("未扫到有未释放锁仓金额红包的活动");
            result.setCode(0);
            result.setMessage("定时释放活动锁仓金额成功");
            return result;
        }
        for(ActivityRedPackManage activityRedPackManage : list){
            try{
                activityRedpacketService.realseLockAmount(activityRedPackManage);
            }catch (Exception e){
                e.printStackTrace();
                log.info("释放活动锁仓金额失败Id ：{}，name：{}，",activityRedPackManage.getId());
            }
        }
        result.setCode(0);
        result.setMessage("定时释放活动锁仓金额成功");

        return result;
    }
}
