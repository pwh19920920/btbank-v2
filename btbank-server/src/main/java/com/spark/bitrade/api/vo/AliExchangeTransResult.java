package com.spark.bitrade.api.vo;

import lombok.Data;

@Data
public class AliExchangeTransResult {
    private Integer showapi_res_code;
    private String showapi_res_error;
    private AliExchangeTransVo showapi_res_body;
}
