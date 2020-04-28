package com.spark.bitrade.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * WelfareLockedVo
 *
 * @author biu
 * @since 2020/4/16 14:40
 */
@Data
@ApiModel("福利挖矿锁仓")
@AllArgsConstructor
public class WelfareLockedVo {

    @ApiModelProperty("新人福利挖矿锁仓")
    private BigDecimal newLocked;

    @ApiModelProperty("增值福利挖矿锁仓")
    private BigDecimal incrLocked;

}
