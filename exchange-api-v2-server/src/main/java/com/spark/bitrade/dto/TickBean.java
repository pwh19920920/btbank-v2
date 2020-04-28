package com.spark.bitrade.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author shenzucai
 * @time 2019.12.27 13:39
 */
@NoArgsConstructor
@Data
public class TickBean {
    /**
     * amount : 39509.61768425892
     * open : 7192.21
     * close : 7210
     * high : 7437.6
     * id : 207307301785
     * count : 265665
     * low : 7157.27
     * version : 207307301785
     * ask : [7210.51,0.346727]
     * vol : 2.8660932911457676E8
     * bid : [7210.02,0.001387]
     */

    private BigDecimal amount;
    private BigDecimal open;
    private BigDecimal close;
    private BigDecimal high;
    private BigDecimal id;
    private BigDecimal count;
    private BigDecimal low;
    private BigDecimal version;
    private BigDecimal vol;
    private List<BigDecimal> ask;
    private List<BigDecimal> bid;
}
