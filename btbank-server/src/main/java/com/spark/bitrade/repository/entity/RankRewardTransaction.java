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
 * 奖励金额流水(RankRewardTransaction)表实体类
 *
 * @author daring5920
 * @since 2019-12-17 15:14:10
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "奖励金额流水")
public class RankRewardTransaction {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 奖励类型
     */
    @ApiModelProperty(value = "奖励类型", example = "")
    private Integer rewardType;

    /**
     * 奖励金额
     */
    @ApiModelProperty(value = "奖励金额", example = "")
    private BigDecimal reward;

    /**
     * 奖励等级
     */
    @ApiModelProperty(value = "奖励等级", example = "")
    private Integer rewardLevel;

    /**
     * 奖励时间
     */
    @ApiModelProperty(value = "奖励时间", example = "")
    private Date rewardTime;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", example = "")
    private String username;

    @ApiModelProperty(value = "用户会员Id", example = "")
    private Long memberId;

    /**
     * 用户收益金额
     */
    @ApiModelProperty(value = "用户收益金额", example = "")
    private BigDecimal minerProfit;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间", example = "")
    private Date updateTime;

}