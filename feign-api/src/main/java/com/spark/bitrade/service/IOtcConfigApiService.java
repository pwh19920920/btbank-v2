package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.ProfitVo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 提供配置 API服务
 *
 * @author zhouhf
 * @time 2019.11.29 11:35
 */
@FeignClient(FeignServiceConstant.BTBANK_SERVER)
public interface IOtcConfigApiService {


    @PostMapping(value = "/btbank/inner/miner/getValue")
    MessageRespResult<String> getValue(@RequestParam("key") String key);

}
