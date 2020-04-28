package com.spark.bitrade.job.handler;

import com.spark.bitrade.service.IBtbankServerService;
import com.spark.bitrade.util.MessageRespResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@JobHandler(value = "memberOldMemberReleaseJobHandler")
@Component
public class MemberOldMemberReleaseJobHandler extends IJobHandler {

    @Autowired
    private IBtbankServerService btbankServerService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        boolean flag="on".equalsIgnoreCase(s);
        //判断当前时间
        LocalDateTime dateTime=LocalDateTime.now();
        int dayOfMonth = dateTime.getDayOfMonth();
        if(dayOfMonth!=1&&!flag){
            XxlJobLogger.log("当前时间不是月初1号,不满足发放奖励条件,未执行定时任务");
            log.info("======================当前时间{},不满足发放奖励条件,未执行定时任务=====================",dateTime.toString());
            return ReturnT.SUCCESS;
        }

        MessageRespResult ret = btbankServerService.oldMemberRelease();
        log.info("老矿工推荐福利释放{}",ret);
        return ReturnT.SUCCESS;
    }
}