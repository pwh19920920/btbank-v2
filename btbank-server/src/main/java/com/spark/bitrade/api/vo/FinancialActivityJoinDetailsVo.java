package com.spark.bitrade.api.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Created by mahao on 2019/12/23.
 */
@Data
public class FinancialActivityJoinDetailsVo {
    private Date endTime;

    /**
     * 理财活动参与明细id
     */
    @ApiModelProperty(value = "理财活动参与明细id", example = "")
    private Long id;
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
     * 状态 0 未开始，1参与中，2锁仓中 3已结束
     */
    @ApiModelProperty(value = "状态 0 未开始，1参与中，2锁仓中 3已结束", example = "")
    private Integer status;

    /**
     * 说明
     */
    @ApiModelProperty(value = "说明", example = "")
    private String description;


}
