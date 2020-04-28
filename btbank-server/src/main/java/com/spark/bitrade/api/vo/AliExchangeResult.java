package com.spark.bitrade.api.vo;

import lombok.Data;

import java.util.List;

@Data
public class AliExchangeResult {
    private String showapi_res_error;
    private String showapi_res_id;
    private int showapi_res_code;
    private AliExchangeBody showapi_res_body;
}



