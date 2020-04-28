package com.spark.bitrade.service;

import com.alibaba.fastjson.JSONArray;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 币种实时汇率接口
 * 备注：从market获取汇率
 *
 * @author yangch
 * @time 2019-06-18 09:24:28
 */
//@FeignClient(FeignServiceConstant.MARKET_SERVER)
public interface ICoinExchange {

    /**
     * 获取指定币种的汇率，获取汇率的顺序 USDT、CNYT、BTC、ETH
     *
     * @param coin 币种简称名称，如USDT、USD、CNYT、CNY、BT、BTC、LTC...
     * @return 基于USDT转换后汇率，无汇率则返回0
     */
    @RequestMapping("/market/exchange-rate/usd/{coin}")
    MessageRespResult<BigDecimal> getUsdExchangeRate(@PathVariable(value = "coin") String coin);

    /**
     * 获取指定币种的汇率，获取汇率的顺序 USDT、CNYT、BTC、ETH
     *
     * @param coin 币种简称名称，如USDT、USD、CNYT、CNY、BT、BTC、LTC...
     * @return 基于USDT转换后汇率，无汇率则返回0
     */
    @RequestMapping("/market/exchange-rate/cny/{coin}")
    MessageRespResult<BigDecimal> getCnyExchangeRate(@PathVariable(value = "coin") String coin);

    /**
     * 获取基于cnyt的汇率，获取汇率的顺序 CNYT、USDT、BTC、ETH
     *
     * @param coin 币种简称名称，如USDT、USD、CNYT、CNY、BT、BTC、LTC...
     * @return 基于CNYT转换后汇率，无汇率则返回0
     */
    @RequestMapping("/market/exchange-rate/cnyt/{coin}")
    MessageRespResult<BigDecimal> getCnytExchangeRate(@PathVariable(value = "coin") String coin);

    /**
     * USD对CNY的汇率
     *
     * @return
     */
    @RequestMapping("/market/exchange-rate/usd-cny")
    MessageRespResult<BigDecimal> getUsdCnyRate();

    /**
     * 获取K线
     *
     * @param symbol     交易对
     * @param from       开始时间戳
     * @param to         截止时间戳
     * @param resolution k线类型
     * @return
     */
    @RequestMapping("/market/history")
    public JSONArray findKHistory(@RequestParam(value = "symbol") String symbol,
                                  @RequestParam(value = "from") long from,
                                  @RequestParam(value = "to") long to,
                                  @RequestParam(value = "resolution") String resolution);
}
