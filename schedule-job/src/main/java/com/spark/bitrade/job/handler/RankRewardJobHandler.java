package com.spark.bitrade.job.handler;

import com.spark.bitrade.service.IBtbankServerService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * DemoJobHandler
 *
 * @author zhouhaifeng
 * @since 2019/12/13 18:11
 */
@JobHandler(value = "rankRewardJobHandler")
@Component
public class RankRewardJobHandler extends IJobHandler {

    @Autowired
    private IBtbankServerService btbankServerService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("开始处理收益排行榜奖励发放");
        btbankServerService.rankReward();
        XxlJobLogger.log("奖励发放完毕");

        XxlJobLogger.log("开始处理累计收益排行榜数据写入");
        btbankServerService.totalRankReward();
        XxlJobLogger.log("数据写入完毕");
        return ReturnT.SUCCESS;
    }
}
