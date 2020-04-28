package com.spark.bitrade.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.repository.entity.AdvertiseOperationHistory;
import com.spark.bitrade.repository.entity.FinancialActivityJoinDetails;
import com.spark.bitrade.repository.entity.ForeignCashLocation;
import com.spark.bitrade.repository.entity.ForeignCurrency;
import com.spark.bitrade.repository.service.ForeignCashLocationService;
import com.spark.bitrade.repository.service.ForeignCurrencyService;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * @author mahao
 * @time 2020.0204 09:16
 */
@Slf4j
@Api(tags = {"取现地址控制器"})
@RequestMapping(path = "api/v2/cashlocation", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class ForeignCashLocationController {

    private ForeignCashLocationService foreignCashLocationService;
    private ForeignCurrencyService foreignCurrencyService;
    @ApiOperation(value = "获取取现地址列表", response = ForeignCashLocation.class)
    @PostMapping(value = "list")
    public MessageRespResult<List<ForeignCashLocation>> list() {
        QueryWrapper<ForeignCashLocation> query = new QueryWrapper<>();
        query.lambda().eq(ForeignCashLocation::getStatus,1);
        return MessageRespResult.success4Data(foreignCashLocationService.list(query));
    }

    @ApiOperation(value = "获取兑换币列表", response = ForeignCurrency.class)
    @PostMapping(value = "coinlist")
    public MessageRespResult<List<ForeignCurrency>> getExchangeCoinlist() {
        return MessageRespResult.success4Data(foreignCurrencyService.getAvail());
    }
}
