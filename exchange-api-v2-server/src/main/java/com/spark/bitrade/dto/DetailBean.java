package com.spark.bitrade.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzucai
 * @time 2019.12.27 13:35
 */
@NoArgsConstructor
@Data
public class DetailBean {
    /**
     * coinId : 1
     * coinName : BTC
     * buy : 50340.00
     * sell : 50297.00
     */

    private Integer coinId;
    private String coinName;
    private String buy;
    private String sell;
}
