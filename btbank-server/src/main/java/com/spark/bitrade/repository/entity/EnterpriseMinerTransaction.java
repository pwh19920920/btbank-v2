package com.spark.bitrade.repository.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 企业矿工流水表(EnterpriseMinerTransaction)表实体类
 *
 * @author biu
 * @since 2019-12-24 14:43:00
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "企业矿工流水表")
public class EnterpriseMinerTransaction implements Serializable {

    /**
     * ID
     */
    @TableId
    @ApiModelProperty(value = "ID", example = "")
    private Long id;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID", example = "")
    private Long memberId;

    /**
     * 矿工ID
     */
    @ApiModelProperty(value = "矿工ID", example = "")
    private Integer minerId;

    /**
     * 1：转入 2：转出 3：抢单挖矿 4：挖矿收益
     */
    @ApiModelProperty(value = "1：转入 2：转出 3：抢单挖矿 4：挖矿收益", example = "")
    private Integer type;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量", example = "")
    private BigDecimal amount;

    /**
     * 佣金
     */
    @ApiModelProperty(value = "佣金", example = "")
    private BigDecimal reward;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号", example = "")
    private String orderSn;

    /**
     * 关联ID，资金流水ID
     */
    @ApiModelProperty(value = "关联ID，资金流水ID", example = "")
    private String refId;

    /**
     * 0：未处理 1：已处理
     */
    @ApiModelProperty(value = "0：未处理 1：已处理", example = "")
    private Integer status;

    /**
     * 奖励处理状态 0：未处理 1：已处理
     */
    @ApiModelProperty(value = "奖励处理状态 0：未处理 1：已处理", example = "")
    private Integer rewardStatus;

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