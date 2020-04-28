package com.spark.bitrade.job.handler;

import com.spark.bitrade.service.IBtbankServerService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * DemoJobHandler
 *
 * @author mahao
 * @since 2020/1/17
 */
@JobHandler(value = "recommendUnlock")
@Component
public class RecommendUnlockHandler extends IJobHandler {
    @Autowired
    private IBtbankServerService btbankServerService;
    @Override
    public ReturnT<String> execute(String s) throws Exception {

        btbankServerService.recommendUnlock();

        return ReturnT.SUCCESS;
    }
}
