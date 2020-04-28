package com.spark.bitrade.repository.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 总报表(统计数据表)(BtBankGeneralStatement)表实体类
 *
 * @author daring5920
 * @since 2019-12-16 11:19:04
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "总报表(统计数据表)")
public class BtBankGeneralStatement {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期", example = "")
    private Date time;

    /**
     * 总注册人数
     */
    @ApiModelProperty(value = "总注册人数", example = "")
    private Integer memberRegTotal;

    /**
     * 总矿工人数
     */
    @ApiModelProperty(value = "总矿工人数", example = "")
    private Integer minerTotal;

    /**
     * 当日新增矿工人数
     */
    @ApiModelProperty(value = "当日新增矿工人数", example = "")
    private Integer memberRegToday;

    /**
     * 商家数
     */
    @ApiModelProperty(value = "商家数", example = "")
    private Integer businessTotal;

    /**
     * 当日新增商家数
     */
    @ApiModelProperty(value = "当日新增商家数", example = "")
    private Integer businessNewToday;

    /**
     * 资产总额
     */
    @ApiModelProperty(value = "资产总额", example = "")
    private BigDecimal totalAssets;

    /**
     * 可用余额
     */
    @ApiModelProperty(value = "可用余额", example = "")
    private BigDecimal memberWallerBalance;

    /**
     * 锁仓
     */
    @ApiModelProperty(value = "锁仓", example = "")
    private BigDecimal lockBalance;

    /**
     * 当日释放
     */
    @ApiModelProperty(value = "当日释放", example = "")
    private BigDecimal minerBalance;

    /**
     * 活期宝总额
     */
    @ApiModelProperty(value = "活期宝总额", example = "")
    private BigDecimal hqbBalance;

    /**
     * 矿池总额
     */
    @ApiModelProperty(value = "矿池总额", example = "")
    private BigDecimal minerBalanceTotal;

    /**
     * 累计总收益
     */
    @ApiModelProperty(value = "累计总收益", example = "")
    private BigDecimal minerGotRewardSum;

    /**
     * 当日总收益
     */
    @ApiModelProperty(value = "当日总收益", example = "")
    private BigDecimal minerRewardSumToday;

    /**
     * 当日已购买总数
     */
    @ApiModelProperty(value = "当日已购买总数", example = "")
    private BigDecimal otcBuyNum;

    /**
     * 当日已出售总数
     */
    @ApiModelProperty(value = "当日已出售总数", example = "")
    private BigDecimal otcSaleNum;

    /**
     * 当日充币
     */
    @ApiModelProperty(value = "当日充币", example = "")
    private BigDecimal chargeNum;

    /**
     * 当日提币
     */
    @ApiModelProperty(value = "当日提币", example = "")
    private BigDecimal withdrawNum;

    /**
     * 创建时间（管理后台代码生成所需）
     */
    @ApiModelProperty(value = "创建时间（管理后台代码生成所需）", example = "")
    private Date createTime;

    /**
     * 修改时间（管理后台代码生成所需）
     */
    @ApiModelProperty(value = "修改时间（管理后台代码生成所需）", example = "")
    private Date updateTime;


}