package com.spark.bitrade.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ww
 * @time 2019.11.11 14:37
 */

@Data
@ApiModel("申请金牌矿工等级返回结果")
public class MinerApplyVO {

    /**
     * 申请时返回的状态码
     */
    @ApiModelProperty("状态码")
    private Integer statusCode;

    @ApiModelProperty("返回内容")
    /**
     * 申请时返回的信息
     */
    private String text;


    public MinerApplyVO(int statusCode, String text) {
        this.statusCode = statusCode;
        this.text = text;
    }
}
