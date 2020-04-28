package com.spark.bitrade.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhouhf
 */
@Data
@ApiModel(value = "商家挖矿收益")
public class ProfitVo {
    @ApiModelProperty(value = "昨日收益")
    private BigDecimal yesterdayProfit;

    @ApiModelProperty(value = "累计收益")
    private BigDecimal totalProfit;

    @ApiModelProperty(value = "订单收益比")
    private BigDecimal orderRevenueratio;
}
