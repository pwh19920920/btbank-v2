package com.spark.bitrade.api.dto;

import lombok.Data;

/**
 * @author shenzucai
 * @time 2020.02.14 11:40
 */
@Data
public class FixOrderDto {

    private Long id;
    /**
     * 当前状态
     */
    private Integer currentStatus;
    /**
     * 修改后的状态
     */
    private Integer passStatus;
}
