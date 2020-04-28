package com.spark.bitrade.api.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * CoinThumb
 *
 * @author biu
 * @since 2019/12/18 18:18
 */
@Data
public class CoinThumb {

    private String symbol;
    private BigDecimal open = BigDecimal.ZERO;  //开盘
    private BigDecimal high = BigDecimal.ZERO;   //最高
    private BigDecimal low = BigDecimal.ZERO;    //最低
    private BigDecimal close = BigDecimal.ZERO;   //收盘
    private BigDecimal change = BigDecimal.ZERO.setScale(2);    //涨幅量=收盘-开盘
    private BigDecimal chg = BigDecimal.ZERO.setScale(2);       //涨幅比例=涨幅量/开盘
    private BigDecimal volume = BigDecimal.ZERO.setScale(2);    //24H成交量
    private BigDecimal turnover = BigDecimal.ZERO;               //成交额
    private BigDecimal lastDayClose = BigDecimal.ZERO;          //昨日收盘价
    private BigDecimal usdRate;       //对usd汇率
    private BigDecimal baseUsdRate;  //基币对usd的汇率
}
