package com.spark.bitrade.job.handler;

import com.spark.bitrade.service.IBtbankServerService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author
 * @since 2019/12/13 18:11
 */
@JobHandler(value = "mineOrderStatementJobHandler")
@Component
public class MineOrderStatementJobHandler extends IJobHandler {

    @Autowired
    private IBtbankServerService btbankServerService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("开始处理矿池订单汇总统计写入任务");
        btbankServerService.statMinerOrderTotal();
        XxlJobLogger.log("写入完毕");
        XxlJobLogger.log("开始处理挖矿汇总统计写入任务");
        btbankServerService.statMinerTotal();
        XxlJobLogger.log("写入完毕");
        return ReturnT.SUCCESS;
    }
}
