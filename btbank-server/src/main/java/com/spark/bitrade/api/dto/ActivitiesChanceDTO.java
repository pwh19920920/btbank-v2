package com.spark.bitrade.api.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * ActivitiesChanceDTO
 *
 * @author biu
 * @since 2020/1/8 13:43
 */
@Data
public class ActivitiesChanceDTO {

    private final static BigDecimal MINIMUM_VALUE = new BigDecimal("100");

    private Long id;
    private BigDecimal reward;

    public boolean isAvailable() {
        return reward != null && reward.compareTo(MINIMUM_VALUE) >= 0;
    }
}
