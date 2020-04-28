package com.spark.bitrade.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditCardCommissionRefundVO {

    @ApiModelProperty(value = "手机号")
    private String mobilePhone;

    @ApiModelProperty(value = "手续费")
    private BigDecimal commissionAmount;

    @ApiModelProperty(value = "时间戳")
    private Long timestamp;

    @ApiModelProperty(value ="签名= MD5(约定密钥+commissionAmount+mobilePhone+timestamp)")
    private String sign;

    @ApiModelProperty(value = "唯一hash标识")
    private String hashCode;

    @ApiModelProperty(value = "关联ID")
    private String refId;
}
