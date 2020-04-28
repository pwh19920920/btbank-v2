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

/**
 * 单个交易日云端转入转出（内部转账）排名统计
 */
@Slf4j
@JobHandler(value = "payFastRankStatementJobHandler")
@Component
public class PayFastRankStatementJobHandler extends IJobHandler {

    @Autowired
    private IBtbankServerService btbankServerService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("开始处理单个交易日云端转入转出（内部转账）排名统计写入任务");
        MessageRespResult respResult = btbankServerService.statFastPayRank();
        log.info("result-{}", respResult);
        XxlJobLogger.log("写入完毕");
        return ReturnT.SUCCESS;
    }
}
