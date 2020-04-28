package com.spark.bitrade.job.handler;

import com.spark.bitrade.service.IBtbankServerService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by mahao on 2020/1/2.
 */
@JobHandler(value = "prizeQuizeRecordHandler")
@Component
public class PrizeQuizeRecordHandler extends IJobHandler {

    @Autowired
    private IBtbankServerService btbankServerService;
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        btbankServerService.generatePrizeQuizeRecord();
        return ReturnT.SUCCESS;
    }
}
