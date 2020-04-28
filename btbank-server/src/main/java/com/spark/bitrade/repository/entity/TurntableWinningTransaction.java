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
 * 自动发放记录表(TurntableWinningTransaction)表实体类
 *
 * @author biu
 * @since 2020-01-09 10:05:01
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "自动发放记录表")
public class TurntableWinningTransaction implements Serializable {

    /**
     * 中奖记录ID
     */
    @TableId
    @ApiModelProperty(value = "中奖记录ID", example = "")
    private Long id;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID", example = "")
    private Long memberId;

    /**
     * 奖励发放账户ID
     */
    @ApiModelProperty(value = "奖励发放账户ID", example = "")
    private Long rewardMemberId;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种", example = "")
    private String coinUnit;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量", example = "")
    private BigDecimal amount;

    /**
     * 转出ID
     */
    @ApiModelProperty(value = "转出ID", example = "")
    private String outcomeTxid;

    /**
     * 转入ID
     */
    @ApiModelProperty(value = "转入ID", example = "")
    private String incomeTxid;

    /**
     * 0：转出中 1：转入中 2：转入完成
     */
    @ApiModelProperty(value = "0：转出中 1：转入中 2：转入完成", example = "")
    private Integer state;

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