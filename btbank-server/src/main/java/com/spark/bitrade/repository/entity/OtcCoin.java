package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * (OtcCoin)表实体类
 *
 * @author daring5920
 * @since 2019-11-29 15:55:58
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class OtcCoin {

    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 买入广告最低发布数量
     */
    @ApiModelProperty(value = "买入广告最低发布数量", example = "")
    private BigDecimal buyMinAmount;

    @ApiModelProperty(value = "", example = "")
    private Integer isPlatformCoin;

    /**
     * 交易手续费率
     */
    @ApiModelProperty(value = "交易手续费率", example = "")
    private BigDecimal jyRate;

    /**
     * 交易手续费率（买币）
     */
    @ApiModelProperty(value = "交易手续费率（买币）", example = "")
    private BigDecimal buyJyRate;

    @ApiModelProperty(value = "", example = "")
    private String name;

    @ApiModelProperty(value = "", example = "")
    private String nameCn;

    /**
     * 卖出广告最低发布数量
     */
    @ApiModelProperty(value = "卖出广告最低发布数量", example = "")
    private BigDecimal sellMinAmount;

    @ApiModelProperty(value = "", example = "")
    private Integer sort;

    @ApiModelProperty(value = "", example = "")
    private Integer status;

    @ApiModelProperty(value = "", example = "")
    private String unit;

    /**
     * 最高单笔交易额
     */
    @ApiModelProperty(value = "最高单笔交易额", example = "")
    private BigDecimal tradeMaxLimit;

    /**
     * 最低单笔交易额
     */
    @ApiModelProperty(value = "最低单笔交易额", example = "")
    private BigDecimal tradeMinLimit;

    /**
     * 买币手续费的折扣率
     */
    @ApiModelProperty(value = "买币手续费的折扣率", example = "")
    private BigDecimal feeBuyDiscount;

    /**
     * 卖币手续费的折扣率
     */
    @ApiModelProperty(value = "卖币手续费的折扣率", example = "")
    private BigDecimal feeSellDiscount;

    /**
     * 货币小数位精度（默认为8位）
     */
    @ApiModelProperty(value = "货币小数位精度（默认为8位）", example = "")
    private Integer coinScale;

    /**
     * 发布购买广告时账户最低可用余额要求（针对普通用户上架）
     */
    @ApiModelProperty(value = "发布购买广告时账户最低可用余额要求（针对普通用户上架）", example = "")
    private BigDecimal generalBuyMinBalance;

    /**
     * 使用优惠币种结算的精度（针对普通用户上架广告费用，默认为8位）
     */
    @ApiModelProperty(value = "使用优惠币种结算的精度（针对普通用户上架广告费用，默认为8位）", example = "")
    private Integer generalDiscountCoinScale;

    /**
     * 可用的支付优惠币种（针对普通用户上架广告费用；使用币种的简写名称，默认为SLU）
     */
    @ApiModelProperty(value = "可用的支付优惠币种（针对普通用户上架广告费用；使用币种的简写名称，默认为SLU）", example = "")
    private String generalDiscountCoinUnit;

    /**
     * 支付优惠折扣率(针对普通用户上架广告费用，用小数来表示百分比)
     */
    @ApiModelProperty(value = "支付优惠折扣率(针对普通用户上架广告费用，用小数来表示百分比)", example = "")
    private BigDecimal generalDiscountRate;

    /**
     * 普通用户上架广告费用
     */
    @ApiModelProperty(value = "普通用户上架广告费用", example = "")
    private BigDecimal generalFee;

    /**
     * 普通用户上架广告费用的币种（使用币种的简写名称，默认为USDT）
     */
    @ApiModelProperty(value = "普通用户上架广告费用的币种（使用币种的简写名称，默认为USDT）", example = "")
    private String generalFeeCoinUnit;


}