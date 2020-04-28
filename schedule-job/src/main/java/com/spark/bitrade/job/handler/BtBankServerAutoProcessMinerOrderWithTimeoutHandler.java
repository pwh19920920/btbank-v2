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
 * BtBankServerAutoProcessMinerOrderWithTimeout
 *
 * @author biu
 * @since 2019/12/11 19:11
 */
@Component
@AllArgsConstructor
@JobHandler(value = "btBankServerAutoProcessMinerOrderWithTimeoutHandler")
public class BtBankServerAutoProcessMinerOrderWithTimeoutHandler extends IJobHandler {

    private IBtbankServerService btBankServerService;

    @Override
    public ReturnT<String> execute(String s) {
        XxlJobLogger.log("开始处理超时30分钟的矿池订单");

        MessageRespResult result = btBankServerService.autoProcess();

        String format = String.format("任务执行结果 [ code = %d, message = '%s', data = '%s' ] ",
                result.getCode(), result.getMessage(), result.getData());
        XxlJobLogger.log(format);

        return ReturnT.SUCCESS;
    }
}