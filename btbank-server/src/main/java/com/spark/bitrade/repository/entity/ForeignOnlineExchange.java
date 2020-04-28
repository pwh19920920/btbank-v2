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
 * 换汇线上订单表(ForeignOnlineExchange)表实体类
 *
 * @author yangch
 * @since 2020-02-04 11:49:34
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "换汇线上订单表")
public class ForeignOnlineExchange {

    /**
     * 主键
     */
    @TableId
    @ApiModelProperty(value = "主键", example = "")
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", example = "")
    private Long memberId;

    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名", example = "")
    private String realName;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码", example = "")
    private String phoneNumber;

    /**
     * 购买数量
     */
    @ApiModelProperty(value = "购买数量", example = "")
    private BigDecimal buyCount;

    /**
     * 手续费
     */
    @ApiModelProperty(value = "手续费", example = "")
    private BigDecimal serviceCharge;

    /**
     * 汇率
     */
    @ApiModelProperty(value = "汇率", example = "")
    private BigDecimal exchangeRate;

    /**
     * 换汇币种
     */
    @ApiModelProperty(value = "换汇币种", example = "")
    private String exchangeSwapCurrency;

    /**
     * 换汇数量
     */
    @ApiModelProperty(value = "换汇数量", example = "")
    private BigDecimal exchangeSwapCount;

    /**
     * 实付BT数量
     */
    @ApiModelProperty(value = "实付BT数量", example = "")
    private BigDecimal actualBtAmount;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号", example = "")
    private Long refId;

    /**
     * 状态:1处理中、2已完成
     */
    @ApiModelProperty(value = "状态:1处理中、2已完成", example = "")
    private Integer orderStatus;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人", example = "")
    private Long opertionId;

    /**
     * 流水号
     */
    @ApiModelProperty(value = "流水号", example = "")
    private Long serialNumber;

    /**
     * 银行信息id
     */
    @ApiModelProperty(value = "银行信息id", example = "")
    private Long bankId;

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
     * 换汇币种中文
     */
    @ApiModelProperty(value = "换汇币种中文", example = "")
    private String exchangeCurrencyName;

    /**
     * 此订单与归集账号的完成状态
     */
    @ApiModelProperty(value = "状态:0归集账号未完成 1归集账号完成", example = "")
    private Integer completeStatus;



    /**
     * 购汇归集状态
     */
    @ApiModelProperty(value = "购汇归集0：没有归集，1已经归集", example = "")
    private Integer collectStatus;

}