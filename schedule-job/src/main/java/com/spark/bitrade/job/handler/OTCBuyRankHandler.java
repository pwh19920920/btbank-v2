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
@JobHandler(value = "otcBuyRankHandler")
@Component
public class OTCBuyRankHandler extends IJobHandler {
    @Autowired
    private IBtbankServerService btbankServerService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("开始处理otc购买快照写入任务");
        MessageRespResult result = btbankServerService.statOTCBuyRank();
        XxlJobLogger.log("写入完毕");
        return ReturnT.SUCCESS;
    }
}
