package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 福利包活动参与明细(WelfareInvolvement)表实体类
 *
 * @author biu
 * @since 2020-04-16 10:15:16
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "福利包活动参与明细")
public class WelfareInvolvement implements Serializable {

    /**
     * ID
     */
    @TableId
    @ApiModelProperty(value = "ID", example = "")
    private Long id;

    /**
     * 活动ID
     */
    @ApiModelProperty(value = "活动ID", example = "")
    private Integer actId;

    /**
     * 活动名称
     */
    @ApiModelProperty(value = "活动名称", example = "")
    private String actName;

    /**
     * 0:新人福利包 1:增值福利包
     */
    @ApiModelProperty(value = "0:新人福利包 1:增值福利包", example = "")
    private Integer actType;

    /**
     * 当天上午10:00
     */
    @ApiModelProperty(value = "当天上午10:00", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date openningTime;

    /**
     * 当天晚上22:00
     */
    @ApiModelProperty(value = "当天晚上22:00", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date closingTime;

    /**
     * 封盘时间往后推13天12小时，统一释放
     */
    @ApiModelProperty(value = "封盘时间往后推13天12小时，统一释放", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date releaseTime;

    /**
     * 参与人id
     */
    @ApiModelProperty(value = "参与人id", example = "")
    private Long memberId;

    /**
     * 参与人昵称
     */
    @ApiModelProperty(value = "参与人昵称", example = "")
    private String username;

    /**
     * 参与金额 固定1w BT
     */
    @ApiModelProperty(value = "参与金额 固定1w BT", example = "")
    private BigDecimal amount;

    /**
     * 推荐人
     */
    @ApiModelProperty(value = "推荐人", example = "")
    private Long inviteId;

    /**
     * 关联转账记录ID
     */
    @ApiModelProperty(value = "关联转账记录ID", example = "")
    private String refId;

    /**
     * 关联撤回记录ID
     */
    @ApiModelProperty(value = "关联撤回记录ID", example = "")
    private String refundRefId;

    /**
     * 固定 1w * 0.075
     */
    @ApiModelProperty(value = "固定 1w * 0.075", example = "")
    private BigDecimal earningUnreleasedAmount;

    /**
     * 具体释放金额
     */
    @ApiModelProperty(value = "具体释放金额", example = "")
    private BigDecimal earningReleaseAmount;

    /**
     * 具体释放时间
     */
    @ApiModelProperty(value = "具体释放时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date earningReleaseTime;

    /**
     * 释放关联记录
     */
    @ApiModelProperty(value = "释放关联记录", example = "")
    private String earningRefId;

    /**
     * 直推奖励金额
     */
    @ApiModelProperty(value = "直推奖励金额", example = "")
    private BigDecimal recommendAmount;

    /**
     * 直推奖励释放时间
     */
    @ApiModelProperty(value = "直推奖励释放时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date recommendReleaseTime;

    /**
     * 直推释放关联记录
     */
    @ApiModelProperty(value = "直推释放关联记录", example = "")
    private String recommendRefId;

    /**
     * 直推奖励状态 0 :未发放 1: 已发放 2:已领取
     */
    @ApiModelProperty(value = "直推奖励状态 0 :未发放 1: 已发放 2:已领取", example = "")
    private Integer recommendStatus;

    /**
     * 金牌佣金释放关联记录
     */
    @ApiModelProperty(value = "金牌佣金释放关联记录", example = "")
    private String goldRefId;

    /**
     * 0：锁仓中  1：已撤回 封盘之前可撤回
     */
    @ApiModelProperty(value = "0：锁仓中  1：已撤回 封盘之前可撤回", example = "")
    private Integer status;

    /**
     * 0：未释放 1：释放本金 2：释放利息 3：释放直推 4：释放金牌
     */
    @ApiModelProperty(value = "0：未释放 1：释放本金 2：释放利息 3：释放直推 4：释放金牌", example = "")
    private Integer releaseStatus;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;


}