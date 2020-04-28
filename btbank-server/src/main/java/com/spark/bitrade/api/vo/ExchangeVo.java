package com.spark.bitrade.api.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeVo {
    private BigDecimal value;
    private String text;
    private Long timestamp;
}
