package com.spark.bitrade.job.handler;

import com.spark.bitrade.service.IBtbankServerService;
import com.spark.bitrade.util.MessageRespResult;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * BtBankServerMinerRebateCheckHandler
 *
 * @author biu
 * @since 2019/12/9 19:39
 */
@JobHandler(value = "btBankServerMinerRebateCheckHandler")
@Component
@AllArgsConstructor
public class BtBankServerMinerRebateCheckHandler extends IJobHandler {

    private IBtbankServerService btBankServerService;

    @Override
    public ReturnT<String> execute(String s) {
        XxlJobLogger.log("开始处理奖励检查任务 param = " + s);

        MessageRespResult result = btBankServerService.checkMinerRebate(s);

        String format = String.format("奖励检查任务执行结果 [ code = %d, message = '%s' ] ", result.getCode(), result.getMessage());
        XxlJobLogger.log(format);

        return ReturnT.SUCCESS;
    }
}
