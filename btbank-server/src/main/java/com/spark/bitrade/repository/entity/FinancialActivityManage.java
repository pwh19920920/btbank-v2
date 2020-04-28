package com.spark.bitrade.repository.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 理财活动管理表(FinancialActivityManage)表实体类
 *
 * @author daring5920
 * @since 2019-12-21 11:49:44
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "理财活动管理表")
public class FinancialActivityManage {

    /**
     * 理财活动id
     */
    @TableId
    @ApiModelProperty(value = "理财活动id", example = "")
    private Long id;

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
     * 可够总份数(整数)
     */
    @ApiModelProperty(value = "可购总份数(整数)", example = "")
    private Integer totalPurchaseNums;

    /**
     * 剩余总份数(整数)
     */
    @ApiModelProperty(value = "剩余总份数(整数)", example = "")
    private Integer remainPurchaseNums;

    /**
     * 活动币种
     */
    @ApiModelProperty(value = "活动币种", example = "")
    private String unit;


    /**
     * 单人可购份额上限
     */
    @ApiModelProperty(value = "单人可购份额上限", example = "")
    private Integer upSinglePurchase;

    /**
     * 每份金额
     */
    @ApiModelProperty(value = "每份金额", example = "")
    private BigDecimal perAmount;

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

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人", example = "")
    private Long creator;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人", example = "")
    private Long updater;

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


}