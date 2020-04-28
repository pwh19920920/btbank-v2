package com.spark.bitrade.job.handler;

import com.spark.bitrade.service.IBtbankServerService;
import com.spark.bitrade.util.MessageRespResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * DemoJobHandler
 *
 * @author zhouhaifeng
 * @since 2019/12/13 18:11
 */
@JobHandler(value = "generalStatementJobHandler")
@Component
public class GeneralStatementJobHandler extends IJobHandler {

    @Autowired
    private IBtbankServerService btbankServerService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("开始处理处理总报表数据查询写入任务");
        btbankServerService.generalStatement();
        XxlJobLogger.log("写入完毕");
        return ReturnT.SUCCESS;
    }
}
