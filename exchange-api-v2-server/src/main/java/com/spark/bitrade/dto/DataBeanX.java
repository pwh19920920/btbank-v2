package com.spark.bitrade.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author shenzucai
 * @time 2019.12.30 13:45
 */
@NoArgsConstructor
@Data
public class DataBeanX {
    /**
     * amount : 349511.44812152913
     * open : 131.97
     * close : 135.14
     * high : 138
     * id : 1577635200
     * count : 93608
     * low : 131.85
     * vol : 4.6995656985165596E7
     */

    private BigDecimal amount;
    private BigDecimal open;
    private BigDecimal close;
    private BigDecimal high;
    private BigDecimal id;
    private BigDecimal count;
    private BigDecimal low;
    private BigDecimal vol;
}
