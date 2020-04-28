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
 * 往期竞猜记录(PrizeQuizeRecord)表实体类
 *
 * @author daring5920
 * @since 2020-01-02 09:58:28
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "往期竞猜记录")
public class PrizeQuizeRecord {

    /**
     * 主键
     */
    @TableId
    @ApiModelProperty(value = "主键", example = "")
    private Integer id;

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
     * 投注开始时间
     */
    @ApiModelProperty(value = "投注开始时间", example = "")
    private Date startTime;

    /**
     * 禁止投注时间
     */
    @ApiModelProperty(value = "禁止投注时间", example = "")
    private Date finalizeTime;

    /**
     * 开奖时间
     */
    @ApiModelProperty(value = "开奖时间", example = "")
    private Date rewardResultTime;

    /**
     * 奖励发放时间
     */
    @ApiModelProperty(value = "奖励发放时间", example = "")
    private Date rewardReleaseTime;

    /**
     * 投注场数
     */
    @ApiModelProperty(value = "投注场数", example = "")
    private Integer prizeQuizCount;

    /**
     * 最高分红
     */
    @ApiModelProperty(value = "最高分红", example = "")
    private BigDecimal maxReward;

    /**
     * 最高分红用户ID
     */
    @ApiModelProperty(value = "最高分红用户ID", example = "")
    private Long maxRewardMemberId;

    /**
     * 竞猜状态，0未开始，1开始，2结束
     */
    @ApiModelProperty(value = "竞猜状态，0未开始，1开始，2结束", example = "")
    private Integer type;

    /**
     * 涨投注人数
     */
    @ApiModelProperty(value = "涨投注人数", example = "")
    private Integer upNum;

    /**
     * 跌投注人数
     */
    @ApiModelProperty(value = "跌投注人数", example = "")
    private Integer downNum;

    /**
     * 总投注人数
     */
    @ApiModelProperty(value = "总投注人数", example = "")
    private Integer totalNum;

    /**
     * 跌投注金额
     */
    @ApiModelProperty(value = "跌投注金额", example = "")
    private BigDecimal downAmount;

    /**
     * 涨投注金额
     */
    @ApiModelProperty(value = "涨投注金额", example = "")
    private BigDecimal upAmount;

    /**
     * 总投注金额
     */
    @ApiModelProperty(value = "总投注金额", example = "")
    private BigDecimal totalAmount;

    /**
     * 竞猜结果0-跌1-涨
     */
    @ApiModelProperty(value = "竞猜结果0-跌1-涨", example = "")
    private Integer priQuizeResult;

    /**
     * 竞猜币种
     */
    @ApiModelProperty(value = "竞猜币种", example = "")
    private String coinUnit;

    /**
     * 分红总金额
     */
    @ApiModelProperty(value = "分红总金额", example = "")
    private BigDecimal rewardAmount;

    /**
     * 平台分红
     */
    @ApiModelProperty(value = "平台分红", example = "")
    private BigDecimal platformAmount;


    /**
     * 当天12点价格
     */
    @ApiModelProperty(value = "当天12点价格", example = "")
    private BigDecimal currentAmout;



    /**
     * 当天12点价格
     */
    @ApiModelProperty(value = "当天12点价格", example = "")
    private BigDecimal tomorrowAmount;
}