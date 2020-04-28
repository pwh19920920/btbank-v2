package com.spark.bitrade.api.controller;


import com.spark.bitrade.api.vo.OtcWithdrawVO;
import com.spark.bitrade.repository.entity.RankRewardTransaction;
import com.spark.bitrade.repository.service.RankRewardTransactionService;
import com.spark.bitrade.util.DateUtil;
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

import java.util.Date;
import java.util.List;

/**
 * @author mahao
 * @time 2019.11.29 09:16
 */
@Slf4j
@Api(tags = {"收益排行榜"})
@RequestMapping(path = "api/v2/rank", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class RankController {
    @Autowired
    private RankRewardTransactionService rankRewardTransactionService;

    @ApiOperation(value = "每日挖矿收益排行榜", response = RankRewardTransaction.class)
    @PostMapping(value = "/no-auth/dayProfitRankList")
    public MessageRespResult<List<RankRewardTransaction>> dayProfitRankList() {

        RankRewardTransaction rankRewardTransaction = new RankRewardTransaction();
        rankRewardTransaction.setCreateTime(DateUtil.addDay(new Date(),-1));
        rankRewardTransaction.setRewardType(1);
        List<RankRewardTransaction> list = rankRewardTransactionService.getRankListByType( rankRewardTransaction);
        if(list.size()==0){
            rankRewardTransaction.setCreateTime(DateUtil.addDay(new Date(),-2));
            list = rankRewardTransactionService.getRankListByType( rankRewardTransaction);
        }
        return MessageRespResult.success4Data(list);
    }
    @ApiOperation(value = "每日推广收益排行榜", response = RankRewardTransaction.class)
    @PostMapping(value = "/no-auth/dayMinerRankList")
    public MessageRespResult<List<RankRewardTransaction>> dayMinerRankList() {
        RankRewardTransaction rankRewardTransaction = new RankRewardTransaction();
        rankRewardTransaction.setCreateTime(DateUtil.addDay(new Date(),-1));
        rankRewardTransaction.setRewardType(2);
        List<RankRewardTransaction> list = rankRewardTransactionService.getRankListByType( rankRewardTransaction);
        if(list.size()==0){
            rankRewardTransaction.setCreateTime(DateUtil.addDay(new Date(),-2));
            list = rankRewardTransactionService.getRankListByType( rankRewardTransaction);
        }
        return MessageRespResult.success4Data(list);
    }
    @ApiOperation(value = "累积收益排行榜", response = RankRewardTransaction.class)
    @PostMapping(value = "/no-auth/minerRewardRankList")
    public MessageRespResult<List<RankRewardTransaction>> minerRewardRankList() {
        RankRewardTransaction rankRewardTransaction = new RankRewardTransaction();
        rankRewardTransaction.setCreateTime(DateUtil.addDay(new Date(),-1));
        rankRewardTransaction.setRewardType(3);
        List<RankRewardTransaction> list = rankRewardTransactionService.getRankListByType( rankRewardTransaction);
        if(list.size()==0){
            rankRewardTransaction.setCreateTime(DateUtil.addDay(new Date(),-2));
            list = rankRewardTransactionService.getRankListByType( rankRewardTransaction);
        }
        return MessageRespResult.success4Data(list);
    }
}
