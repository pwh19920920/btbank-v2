package com.spark.bitrade.api.controller;

import com.spark.bitrade.biz.SatisticsService;
import com.spark.bitrade.repository.entity.*;
import com.spark.bitrade.repository.service.BtBankGeneralStatementService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author zhouhf
 * @time 2019.11.29 09:16
 */
@Slf4j
@Api(tags = {"报表统计"})
@RequestMapping(path = "statistics/report", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class StatisticsController {

    @Autowired
    BtBankGeneralStatementService btBankGeneralStatementService;
    @Autowired
    SatisticsService satisticsService;

    @ApiOperation(value = "总报表统计查询", response = BtBankGeneralStatement.class)
    @PostMapping(value = "generalStatement")
    public MessageRespResult<BtBankGeneralStatement> generalStatement() {
        LocalDateTime startTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        Boolean flag = btBankGeneralStatementService.selectTotal(startTime, LocalDate.now().minusDays(1));
        if (flag) {
            return MessageRespResult.success();
        } else {
            return MessageRespResult.error("查询写入总报表失败");
        }

    }

    @ApiOperation(value = "矿池订单汇总统计", response = BtBankMinerOrderTotal.class)
    @PostMapping(value = "statMinerOrderTotal")
    public MessageRespResult<BtBankMinerOrderTotal> statMinerOrderTotal() {
        Boolean flag = satisticsService.statMinerOrderTotal();
        if (flag) {
            return MessageRespResult.success();
        } else {
            return MessageRespResult.error("查询写入矿池订单汇总报表失败");
        }
    }

    @ApiOperation(value = "挖矿汇总统计", response = BtBankMinerTotal.class)
    @PostMapping(value = "statMinerTotal")
    public MessageRespResult<BtBankMinerTotal> statMinerTotal() {
        Boolean flag = satisticsService.statMinerTotal();
        if (flag) {
            return MessageRespResult.success();
        } else {
            return MessageRespResult.error("查询写入挖矿汇总统计报表失败");
        }
    }

    @ApiOperation(value = "企业挖矿汇总统计", response = EnterpriseMinerTotal.class)
    @PostMapping(value = "statEnterpriseMineTotal")
    public MessageRespResult<EnterpriseMinerTotal> statEnterpriseMineTotal() {
        Boolean flag = satisticsService.statEnterpriseMineTotal();
        if (flag) {
            return MessageRespResult.success();
        } else {
            return MessageRespResult.error("查询写入企业挖矿汇总统计报表失败");
        }
    }

    @ApiOperation(value = "用户每日资产统计")
    @PostMapping(value = "statUserAssets")
    public MessageRespResult<TotalDailyAmount> statUserAssets() {
        Boolean aBoolean = satisticsService.statTotalDailyAmount();
        if (aBoolean) {
            return MessageRespResult.success();
        } else {
            return MessageRespResult.error("用户每日资产统计失败");
        }

    }

    @ApiOperation(value = "业绩排名快照", response = EnterpriseMinerTotal.class)
    @PostMapping(value = "statPerRanking")
    public MessageRespResult<BtBankMinerPerRank> statPerRanking() {
        Boolean flag = satisticsService.statPerRanking();
        if (flag) {
            return MessageRespResult.success();
        } else {
            return MessageRespResult.error("查询写入业绩排名快照报表失败");
        }
    }

    @ApiOperation(value = "单个交易日云端转入转出（内部转账）排名", response = EnterpriseMinerTotal.class)
    @PostMapping(value = "statFastPayRank")
    public MessageRespResult<SingleDailyStatistics> statFastPayRank() {
        Boolean flag = satisticsService.statFastPayRank();
        if (flag) {
            return MessageRespResult.success();
        } else {
            return MessageRespResult.error("查询写入单个交易日云端转入转出（内部转账）排名数据失败");
        }
    }

    @ApiOperation(value = "单个交易日OTC统计", response = SingleDealStatistics.class)
    @PostMapping(value = "statSingleTransactionOTC")
    public MessageRespResult<SingleDailyStatistics> statSingleTransactionOTC() {
        Boolean flag = satisticsService.statSingleTransactionOTC();
        if (flag) {
            return MessageRespResult.success();
        } else {
            return MessageRespResult.error("查询写入单个交易日OTC统计快照报表失败");
        }
    }

    @ApiOperation(value = "单个交易日OTC购买排名", response = SingleDailyStatistics.class)
    @PostMapping(value = "statOTCBuyRank")
    public MessageRespResult<SingleDailyStatistics> statOTCBuyRank() {
        Boolean flag = satisticsService.statOTCTransactionBuy();
        if (flag) {
            return MessageRespResult.success();
        } else {
            return MessageRespResult.error("查询写入单个交易日OTC购买排名快照报表失败");
        }
    }

    @ApiOperation(value = "单个交易日OTC出售排名", response = SingleDailyStatistics.class)
    @PostMapping(value = "statOTCSellRank")
    public MessageRespResult<SingleDailyStatistics> statOTCSellRank() {
        Boolean flag = satisticsService.statOTCTransactionSell();
        if (flag) {
            return MessageRespResult.success();
        } else {
            return MessageRespResult.error("查询写入单个交易日OTC购买排名快照报表失败");
        }
    }

}
