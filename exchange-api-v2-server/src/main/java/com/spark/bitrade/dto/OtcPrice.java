package com.spark.bitrade.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenzucai
 * @time 2019.12.27 13:34
 */
@NoArgsConstructor
@Data
public class OtcPrice {

    /**
     * success : true
     * code : 200
     * message : 成功
     * data : {"countryId":37,"currencyId":1,"detail":[{"coinId":1,"coinName":"BTC","buy":"50340.00","sell":"50297.00"},{"coinId":3,"coinName":"ETH","buy":"876.78","sell":"876.00"},{"coinId":2,"coinName":"USDT","buy":"6.97","sell":"6.97"},{"coinId":4,"coinName":"HT","buy":"19.41","sell":"19.26"},{"coinId":5,"coinName":"EOS","buy":"17.76","sell":"17.68"},{"coinId":7,"coinName":"XRP","buy":"1.32","sell":"1.31"},{"coinId":8,"coinName":"LTC","buy":"280.43","sell":"278.81"},{"coinId":6,"coinName":"HUSD","buy":"7.03","sell":"6.98"},{"coinId":10,"coinName":"BCH","buy":null,"sell":null}]}
     */

    private boolean success;
    private Integer code;
    private String message;
    private DataBean data;
}
