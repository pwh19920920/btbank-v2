package com.spark.bitrade.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ActivitiesChanceVO
 *
 * @author biu
 * @since 2020/1/7 17:02
 */
@Data
@ApiModel("抽奖机会")
public class ActivitiesChanceVO {

    @ApiModelProperty("活动ID")
    private Integer actId;

    @ApiModelProperty("次数")
    private Integer number;
}
