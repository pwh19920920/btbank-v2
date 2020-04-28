package com.spark.bitrade.repository.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * (ActivityRedPackReceiveRecord)表实体类
 *
 * @author yangch
 * @since 2020-01-13 10:44:03
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class ActivityRedPackReceiveRecord {

    /**
     * 主键
     */
    @TableId
    @ApiModelProperty(value = "主键", example = "")
    private Long id;

    /**
     * 活动ID
     */
    @ApiModelProperty(value = "活动ID", example = "")
    private Long redpackId;

    /**
     * 活动名称
     */
    @ApiModelProperty(value = "活动名称", example = "")
    private String redpackName;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id", example = "")
    private Long memberId;

    /**
     * 领取币种
     */
    @ApiModelProperty(value = "领取币种", example = "")
    private String receiveUnit;

    /**
     * 领取数量
     */
    @ApiModelProperty(value = "领取数量", example = "")
    private BigDecimal receiveAmount;

    /**
     * 领取时间
     */
    @ApiModelProperty(value = "领取时间", example = "")
    private Date receiveTime;

    /**
     * 用户类型:{1:新会员,2:游客, 3:老会员}
     */
    @ApiModelProperty(value = "用户类型:{1:新会员,2:游客, 3:老会员}", example = "")
    private Short userType;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    private Date updateTime;

    /**
     * 领取限时
     */
    @ApiModelProperty(value = "领取限时", example = "")
    private Integer within;

    /**
     * 领取状态领取状态{0:未领取1:已领取,2:已收回}
     */
    @ApiModelProperty(value = "领取状态领取状态{0:未领取1:已领取,2:已收回}", example = "")
    private Short receiveStatus;
    /**
     * 触发事件 {0抢单挖矿 1推荐有效矿工}
     */
    @ApiModelProperty(value = "触发事件{0抢单挖矿 1推荐有效矿工}", example = "")
    private Short triggerEvent;
    /**
     * 下级ID
     */
    @ApiModelProperty(value = "下级ID", example = "")
    private Long subMemberId;

}