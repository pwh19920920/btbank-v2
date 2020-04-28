package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;

/**
 * (TotalDailyAmount)实体类
 *
 * @author qiuyuanjie
 * @since 2020-03-09 16:48:01
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "用户资产每日汇总")
public class TotalDailyAmount {
    //主键
    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;
    //每天统计日期
    @ApiModelProperty(value = "每天统计日期", example = "")
    private Date createTime;
    //币种
    @ApiModelProperty(value = "币种", example = "")
    private String coinId;
    //会员总额
    @ApiModelProperty(value = "会员总额", example = "")
    private BigDecimal memberTotal = BigDecimal.ZERO;
    //可用余额
    @ApiModelProperty(value = "可用余额", example = "")
    private BigDecimal balance = BigDecimal.ZERO;
    //冻结币数
    @ApiModelProperty(value = "冻结币数", example = "")
    private BigDecimal frozenBalance = BigDecimal.ZERO;
    //锁仓数量
    @ApiModelProperty(value = "锁仓数量", example = "")
    private BigDecimal lockBalance = BigDecimal.ZERO;
    //活期宝币数
    @ApiModelProperty(value = "活期宝币数", example = "")
    private BigDecimal hqbAmout = BigDecimal.ZERO;
    //大宗挖矿
    @ApiModelProperty(value = "大宗挖矿", example = "")
    private BigDecimal bulkMining = BigDecimal.ZERO;
    //矿池可用
    @ApiModelProperty(value = "矿池可用", example = "")
    private BigDecimal minerBalanceAmount = BigDecimal.ZERO;
    //矿池锁仓
    @ApiModelProperty(value = "矿池锁仓", example = "")
    private BigDecimal minerLockAmount = BigDecimal.ZERO;
    //企业矿池
    @ApiModelProperty(value = "企业矿池", example = "")
    private BigDecimal enterPriseOrepool = BigDecimal.ZERO;
    //红包锁仓
    @ApiModelProperty(value = "红包锁仓", example = "")
    private BigDecimal redLockAmount = BigDecimal.ZERO;

}