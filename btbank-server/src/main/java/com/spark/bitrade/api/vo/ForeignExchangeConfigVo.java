package com.spark.bitrade.api.vo;

import com.spark.bitrade.constant.BtBankSystemConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ForeignExchangeConfigVo {
    @ApiModelProperty(value = "换汇线下最低限制", example = "")
    BigDecimal  limitAmount ;
    @ApiModelProperty(value = "线上取现手续费", example = "")
    BigDecimal  onlineRate;
    @ApiModelProperty(value = "线下取现手续费", example = "")
    BigDecimal offineRate;
    @ApiModelProperty(value = "线上换汇开关", example = "")
    Boolean  offineSwitch ;
    @ApiModelProperty(value = "线下换汇开关", example = "")
    Boolean  onineSwitch ;
    @ApiModelProperty(value = "换汇线上最低限制", example = "")
    BigDecimal  limitOnLineAmount ;
}
