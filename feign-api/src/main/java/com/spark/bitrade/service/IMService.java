package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.entity.chat.ImMember;
import com.spark.bitrade.entity.chat.ImResult;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 调用IM云信网接口
 *
 * @author: Zhong Jiang
 * @date: 2020-03-23 15:24
 */
@FeignClient(FeignServiceConstant.IM_SERVER)
public interface IMService {

    /**
     * 添加云信会员
     *
     * @param
     * @return 结果
     */
    @PostMapping("api/v2/imMiner/add/miner")
    MessageRespResult<ImResult> addMiner(@RequestParam("memberId") Long memberId,
                                         @RequestParam("name") String name,
                                         @RequestParam("userType") Integer userType);


}
