package com.spark.bitrade.api.vo;

import lombok.Data;

import java.util.List;
@Data
public class AliExchangeBody {
    private int ret_code;
    private int showapi_fee_code;
    private List<AliExchangeVo> list;
    private int listSize;
}
