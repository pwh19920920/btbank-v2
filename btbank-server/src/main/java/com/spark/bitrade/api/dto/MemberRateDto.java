package com.spark.bitrade.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberRateDto {
    private BigDecimal rate;

    private String comment;
}
