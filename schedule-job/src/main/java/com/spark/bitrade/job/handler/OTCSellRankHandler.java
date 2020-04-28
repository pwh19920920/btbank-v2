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

@Slf4j
@JobHandler(value = "otcSellRankHandler")
@Component
public class OTCSellRankHandler extends IJobHandler {
    @Autowired
    private IBtbankServerService btbankServerService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("开始处理otc出售快照写入任务");
        MessageRespResult result = btbankServerService.statOTCSellRank();
        XxlJobLogger.log("写入完毕");
        return ReturnT.SUCCESS;
    }
}
