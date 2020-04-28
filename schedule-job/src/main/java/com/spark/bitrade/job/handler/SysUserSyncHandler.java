package com.spark.bitrade.job.handler;

import com.spark.bitrade.service.IBtbankServerService;
import com.spark.bitrade.util.MessageRespResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Slf4j
@JobHandler(value = "sysUserSyncHandler")
@Component
public class SysUserSyncHandler extends IJobHandler{
    @Autowired
    private IBtbankServerService btbankServerService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        MessageRespResult ret = btbankServerService.registersysuser();
        log.info("定时同步系统用户{}",ret);
        return ReturnT.SUCCESS;
    }
}
