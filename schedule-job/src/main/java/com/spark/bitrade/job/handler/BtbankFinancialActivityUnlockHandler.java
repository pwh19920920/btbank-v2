package com.spark.bitrade.job.handler;

import com.spark.bitrade.service.IBtbankServerService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.10.02 09:55  
 */
@JobHandler(value = "btbankFinancialActivityUnlockHandler")
@Component
public class BtbankFinancialActivityUnlockHandler extends IJobHandler {

    @Autowired
    private IBtbankServerService btbankServerService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        btbankServerService.autoProfitUnlock();
        return ReturnT.SUCCESS;
    }
}
























