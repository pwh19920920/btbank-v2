package com.spark.bitrade.api.controller;

import com.spark.bitrade.repository.entity.RankRewardTransaction;
import com.spark.bitrade.repository.service.RankRewardTransactionService;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouhf
 * @time 2019.11.29 09:16
 */
@Slf4j
@Api(tags = {"收益排行榜奖励发放"})
@RequestMapping(path = "rank/reward", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class RankRewardController {

    @Autowired
    RankRewardTransactionService rankRewardTransactionService;

    @ApiOperation(value = "收益排行榜奖励发放", response = RankRewardTransaction.class)
    @PostMapping(value = "rankReward")
    public MessageRespResult rankReward() {
        rankRewardTransactionService.getAndInsertReward();
        return MessageRespResult.success("收益发放完成");
    }

    @ApiOperation(value = "累计收益排行榜数据写入", response = RankRewardTransaction.class)
    @PostMapping(value = "totalRankReward")
    public MessageRespResult totalRankReward() {
        rankRewardTransactionService.insertTotalReward();
        return MessageRespResult.success("累计收益数据写入完成");
    }
}
