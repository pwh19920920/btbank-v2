package com.spark.bitrade.api.dto;

import lombok.Data;

import java.util.Date;

/**
 * 直推奖励领取状态DTO
 */
@Data
public class WelfareRewardStateDto {
    private Long id; // 参与记录ID
    private Integer status; // 0:未领取 1:已领取
    private Date receiveTime; // 领取时间
}
