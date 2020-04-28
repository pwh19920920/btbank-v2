package com.spark.bitrade.api.dto;

import lombok.Builder;
import lombok.Data;

/**
 * * @author Administrator * @time 2019.11.29 16:56
 */
@Data
@Builder
public class BankInfo {

    /**
     * 银行
     */
    private String bank;
    /**
     * 支行
     */
    private String branch;
    /**
     * 银行卡号
     */
    private String cardNo;

}
