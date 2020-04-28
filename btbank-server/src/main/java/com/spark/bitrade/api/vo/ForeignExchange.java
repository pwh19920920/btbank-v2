package com.spark.bitrade.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Data
public class ForeignExchange {
    @ApiModelProperty(value = "币种价格", example = "")
    private String symbol;
    private BigDecimal bid;
    private BigDecimal ask;
    @ApiModelProperty(value = "币种价格", example = "")
    private BigDecimal price;
    @ApiModelProperty(value = "价格获取事件", example = "")
    private long timestamp;
    @ApiModelProperty(value = "币种中文", example = "")
    private String name;
    @ApiModelProperty(value = "币种", example = "")
    private String currency;
    @ApiModelProperty(value = "币种ID", example = "")
    private Integer id;
    /**
     * 图片地址
     */
    @ApiModelProperty(value = "图片地址", example = "")
    private String image;
    @ApiModelProperty(value = "币种显示位置", example = "")
    private Integer location;
}
