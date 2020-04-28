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
 * 矿工参与竞猜记录(MinerPrizeQuizeTransaction)表实体类
 *
 * @author daring5920
 * @since 2020-01-02 09:39:36
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "矿工参与竞猜记录")
public class MinerPrizeQuizeTransaction {

    /**
     * 主键
     */
    @TableId
    @ApiModelProperty(value = "主键", example = "")
    private Long id;

    /**
     * 外键关联member表
     */
    @ApiModelProperty(value = "外键关联member表", example = "")
    private Long memberId;

    /**
     * 参与竞猜活动ID
     */
    @ApiModelProperty(value = "参与竞猜活动ID", example = "")
    private Long prieQuizeId;

    /**
     * 投注金额
     */
    @ApiModelProperty(value = "投注金额", example = "")
    private BigDecimal amount;

    /**
     * 参与时间
     */
    @ApiModelProperty(value = "参与时间", example = "")
    private Date createTime;

    /**
     * 竞猜结果0-跌1-涨
     */
    @ApiModelProperty(value = "竞猜结果0-跌1-涨", example = "")
    private Integer prizeQuizeResult;

    /**
     * 投注类型0-跌 1-涨
     */
    @ApiModelProperty(value = "投注类型0-跌 1-涨", example = "")
    private Integer prizeQuizeType;

    /**
     * 竞猜状态 0待公布 1压中 2未压中
     */
    @ApiModelProperty(value = "竞猜状态 0待公布 1压中 2未压中", example = "")
    private Integer guessStatus;

    /**
     * 分红金额
     */
    @ApiModelProperty(value = "分红金额", example = "")
    private Double reward;

    /**
     * 竞猜分红发放时间
     */
    @ApiModelProperty(value = "竞猜分红发放时间", example = "")
    private Date rewardReleaseTime;

    /**
     * 是否扣款，或者释放投注金额 0否 1是
     */
    @ApiModelProperty(value = "是否发放奖励 0否 1是", example = "")
    private Integer releaseStatus;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    private Date updateTime;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", example = "")
    private String userName;

    /**
     * 分红流水
     */
    @ApiModelProperty(value = "分红流水", example = "")
    private String refId;

    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名", example = "")
    private String realName;

    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名", example = "")
    private String mobilePhone;

    /**
     * 是否发放奖励 0否 1是
     */
    @ApiModelProperty(value = "是否发放奖励 0否 1是", example = "")
    private Integer rewardStatus;

}