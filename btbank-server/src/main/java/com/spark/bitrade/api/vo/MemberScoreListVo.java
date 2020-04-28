package com.spark.bitrade.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MemberScoreListVo {

    @ApiModelProperty(value = "id,领取时需要传此id")
    private Long id;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    /**
     * 直推奖励金额
     */
    @ApiModelProperty(value = "金额")
    private BigDecimal wardAmount;

    /**
     * 状态{0:未领取,1:已领取}
     */
    @ApiModelProperty(value = "状态{0:未领取,1:已领取}")
    private Integer status;

    /**
     * 直推矿工ID
     */
    @ApiModelProperty(value = "直推矿工ID")
    private Long childId;

    @ApiModelProperty(value = "直推矿工昵称")
    private String childName;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "记录时间")
    private Date createTime;
    /**
     * 奖励类型 0:直推佣金 1:大宗挖矿直推佣金
     */
    @ApiModelProperty(value = "奖励类型 0:直推佣金 1:大宗挖矿直推佣金")
    private Integer type;



}
