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
@JobHandler(value = "uerAssetsStatHandler")
@Component
public class UerAssetsStatHandler extends IJobHandler {
    @Autowired
    private IBtbankServerService btbankServerService;


    @Override
    public ReturnT<String> execute(String s) throws Exception {
        MessageRespResult rt = btbankServerService.statUserAssets();
        log.info("定时统计用户资产{}",rt);
        return ReturnT.SUCCESS;
    }
}
