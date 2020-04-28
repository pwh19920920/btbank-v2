package com.spark.bitrade.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzucai
 * @time 2019.12.27 13:38
 */
@NoArgsConstructor
@Data
public class ExchangeMarketTickers {

    /**
     * status : ok
     * ch : market.btcusdt.detail.merged
     * ts : 1577425055608
     * tick : {"amount":39509.61768425892,"open":7192.21,"close":7210,"high":7437.6,"id":207307301785,"count":265665,"low":7157.27,"version":207307301785,"ask":[7210.51,0.346727],"vol":2.8660932911457676E8,"bid":[7210.02,0.001387]}
     */

    private String status;
    private String ch;
    private Long ts;
    private TickBean tick;
}
