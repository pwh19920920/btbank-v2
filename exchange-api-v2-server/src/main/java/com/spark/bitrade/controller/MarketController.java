package com.spark.bitrade.controller;

import com.google.common.collect.Sets;
import com.spark.bitrade.config.AutoRefreshTask;
import com.spark.bitrade.controller.vo.CoinThumb;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MarketController
 *
 * @author biu
 * @since 2019/12/18 18:17
 */
@Api(tags = "行情代理控制器")
@RestController
@RequestMapping(value = {"api/v2/market", "api/v2/market/no-auth"})
@AllArgsConstructor
public class MarketController extends ApiController {

    private static final Set<String> coins = Sets.newHashSet("USDT", "BTC", "ETH");

    @ApiOperation(value = "行情接口", notes = "获取币种交易行情")
    @GetMapping("/thumbs")
    public MessageRespResult<List<CoinThumb>> thumbs() {

        List<CoinThumb> thumbs = CollectionUtils.arrayToList(AutoRefreshTask.thumbs.values().toArray());;
        if (thumbs == null) {
            return MessageRespResult.success4Data(Collections.emptyList());
        }

        return MessageRespResult.success4Data(thumbs.stream().filter(this::include).collect(Collectors.toList()));
    }

    private boolean include(CoinThumb thumb) {
        String[] values = thumb.getSymbol().split("/");
        return values[1].equals("BT") && coins.contains(values[0]);
    }
}
