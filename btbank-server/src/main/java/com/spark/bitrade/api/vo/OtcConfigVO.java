package com.spark.bitrade.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ww
 * @time 2019.12.01 16:19
 */
@Data
@ApiModel("OTC配置参数")
public class OtcConfigVO {

    @ApiModelProperty("商家出售奖励比例")
    Object businessSaleRewardRate;
    @ApiModelProperty("自动下架广告最低余额限制")
    Object autoPullOffReminBalanceLimit;
}
