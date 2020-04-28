package com.spark.bitrade.job.handler;

import com.spark.bitrade.service.IBtbankServerService;
import com.spark.bitrade.util.MessageRespResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: Zhong Jiang
 * @date: 2020-03-06 19:04
 */
@Slf4j
@JobHandler(value = "memberExperienceReleaseJobHandler")
@Component
public class MemberExperienceReleaseJobHandler extends IJobHandler {

    @Autowired
    private IBtbankServerService btbankServerService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        MessageRespResult ret = btbankServerService.autoReleaseProfit();
        log.info("3月8日矿工体验金释放或者锁仓{}",ret);
        return ReturnT.SUCCESS;
    }
}
