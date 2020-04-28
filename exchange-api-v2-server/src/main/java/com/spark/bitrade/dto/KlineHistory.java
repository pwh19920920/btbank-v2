package com.spark.bitrade.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author shenzucai
 * @time 2019.12.30 13:44
 */
@NoArgsConstructor
@Data
public class KlineHistory {

    /**
     * status : ok
     * ch : market.ethusdt.kline.1day
     * ts : 1577684635557
     * data : [{"amount":349511.44812152913,"open":131.97,"close":135.14,"high":138,"id":1577635200,"count":93608,"low":131.85,"vol":4.6995656985165596E7}]
     */

    private String status;
    private String ch;
    private long ts;
    private List<DataBeanX> data;
}
