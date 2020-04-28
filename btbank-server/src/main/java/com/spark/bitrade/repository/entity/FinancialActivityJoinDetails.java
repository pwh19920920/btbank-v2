package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 理财活动参与明细(FinancialActivityJoinDetails)表实体类
 *
 * @author daring5920
 * @since 2019-12-21 11:49:42
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "理财活动参与明细")
public class FinancialActivityJoinDetails {

    /**
     * 理财活动参与明细id
     */
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "理财活动参与明细id", example = "")
    private Long id;

    /**
     * 理财活动id
     */
    @ApiModelProperty(value = "理财活动id", example = "")
    private Long activityId;

    /**
     * 活动名称
     */
    @ApiModelProperty(value = "活动名称", example = "")
    private String name;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间", example = "")
    private Date startTime;

    /**
     * 封盘时间
     */
    @ApiModelProperty(value = "封盘时间", example = "")
    private Date finalizeTime;

    /**
     * 释放时间
     */
    @ApiModelProperty(value = "释放时间", example = "")
    private Date releaseTime;

    /**
     * 收益率（大于0的数）
     */
    @ApiModelProperty(value = "收益率（大于0的数）", example = "")
    private BigDecimal profitRate;

    /**
     * 操作类型（0 购买,1 撤回，2 释放）
     */
    @ApiModelProperty(value = "操作类型（0 购买,1 撤回）", example = "")
    private Integer type;

    /**
     * 活动币种
     */
    @ApiModelProperty(value = "活动币种", example = "")
    private String unit;

    /**
     * 参与人id
     */
    @ApiModelProperty(value = "参与人id", example = "")
    private Long memberId;

    /**
     * 参与人昵称
     */
    @ApiModelProperty(value = "参与人昵称", example = "")
    private String memberName;

    /**
     * 操作金额
     */
    @ApiModelProperty(value = "操作金额", example = "")
    private BigDecimal amount;

    /**
     * 购买份数
     */
    @ApiModelProperty(value = "购买份数", example = "")
    private Integer purchaseNums;

    /**
     * 释放利息时间
     */
    @ApiModelProperty(value = "释放利息时间", example = "")
    private Date releaseProfitTime;

    /**
     * 释放利息金额
     */
    @ApiModelProperty(value = "释放利息金额", example = "")
    private BigDecimal releaseProfitAmount;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "撤回时间", example = "")
    private Date updateTime;

    /**
     * 推荐奖励释放时间
     */
    @ApiModelProperty(value = "推荐奖励释放时间", example = "")
    private Date recommendReleaseTime;

    /**
     * 推荐奖励释放金额
     */
    @ApiModelProperty(value = "推荐奖励释放金额", example = "")
    private BigDecimal recommendReleaseAmount;
    /**
     * 推荐奖励释放状态
     */
    @ApiModelProperty(value = "推荐奖励释放状态 {0未释放，1已释放}", example = "")
    private Integer recommendStatus;
    /**
     * 直推人Id
     */
    @ApiModelProperty(value = "直推人Id", example = "")
    private Long inviterId;

    @ApiModelProperty(value = "领取状态默认已领取", example = "")
    private Integer isReceive;
}