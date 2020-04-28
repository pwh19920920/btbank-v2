package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * (OtcOrder)表实体类
 * <p>
 * 【商家挖矿】用户一键提币，商家挖矿，需求2.1.1
 *
 * @author daring5920
 * @since 2019-11-28 17:43:20
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class OtcOrder {

    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 发布广告：广告id，商家挖矿：商家矿池订单id
     */
    @ApiModelProperty(value = "", example = "")
    private Long advertiseId;

    /**
     * 发布广告1卖/0买，商家挖矿0
     */
    @ApiModelProperty(value = "", example = "")
    private Integer advertiseType;

    @ApiModelProperty(value = "", example = "")
    private String aliNo;

    @ApiModelProperty(value = "", example = "")
    private String bank;

    @ApiModelProperty(value = "", example = "")
    private String branch;

    @ApiModelProperty(value = "", example = "")
    private String cardNo;

    @ApiModelProperty(value = "", example = "")
    private Date cancelTime;

    /**
     * 手续费（给平台的）
     */
    @ApiModelProperty(value = "手续费", example = "")
    private BigDecimal commission;

    @ApiModelProperty(value = "", example = "")
    private String country;

    @ApiModelProperty(value = "", example = "")
    private Date createTime;

    /**
     * 发布广告：交易对象id，商家挖矿：用户id
     */
    @ApiModelProperty(value = "", example = "")
    private Long customerId;

    @ApiModelProperty(value = "", example = "")
    private String customerName;

    @ApiModelProperty(value = "", example = "")
    private String customerRealName;

    /**
     * 最高交易额
     */
    @ApiModelProperty(value = "最高交易额", example = "")
    private BigDecimal maxLimit;

    /**
     * 发布广告：广告拥有者id，商家挖矿：商家id
     */
    @ApiModelProperty(value = "", example = "")
    private Long memberId;

    @ApiModelProperty(value = "", example = "")
    private String memberName;

    @ApiModelProperty(value = "", example = "")
    private String memberRealName;

    /**
     * 最低交易额
     */
    @ApiModelProperty(value = "最低交易额", example = "")
    private BigDecimal minLimit;

    /**
     * 交易金额
     */
    @ApiModelProperty(value = "交易金额", example = "")
    private BigDecimal money;

    /**
     * 交易数量
     */
    @ApiModelProperty(value = "交易数量", example = "")
    private BigDecimal number;

    @ApiModelProperty(value = "", example = "")
    private Long orderSn;

    @ApiModelProperty(value = "", example = "")
    private String payMode;

    @ApiModelProperty(value = "", example = "")
    private Date payTime;

    /**
     * 价格
     */
    @ApiModelProperty(value = "价格", example = "")
    private BigDecimal price;

    @ApiModelProperty(value = "", example = "")
    private Date releaseTime;

    /**
     * 商家挖矿：存入“用户一键提币，商家挖矿”
     */
    @ApiModelProperty(value = "", example = "")
    private String remark;

    /**
     * 订单状态：0=已取消/1=未付款/2=已付款/3=已完成/4=申诉中
     */
    @ApiModelProperty(value = "订单状态：0=已取消/1=未付款/2=已付款/3=已完成/4=申诉中", example = "")
    private Integer status;

    @ApiModelProperty(value = "", example = "")
    private Integer timeLimit;

    @ApiModelProperty(value = "", example = "")
    private Long version;

    @ApiModelProperty(value = "", example = "")
    private String wechat;

    @ApiModelProperty(value = "", example = "")
    private Long coinId;

    @ApiModelProperty(value = "", example = "")
    private String qrCodeUrl;

    @ApiModelProperty(value = "", example = "")
    private String qrWeCodeUrl;

    @ApiModelProperty(value = "", example = "")
    private String payCode;

    @ApiModelProperty(value = "", example = "")
    private Integer isManualCancel;

    @ApiModelProperty(value = "", example = "")
    private Long cancelMemberId;

    @ApiModelProperty(value = "", example = "")
    private String epayNo;

    @ApiModelProperty(value = "", example = "")
    private Integer payMethod;

    @ApiModelProperty(value = "", example = "")
    private Date closeTime;

    /**
     * 订单来源类型
     */
    @ApiModelProperty(value = "订单来源类型", example = "")
    private Integer orderSourceType;

    /**
     * 是否为一键交易
     */
    @ApiModelProperty(value = "是否为一键交易", example = "")
    private Integer isOneKey;

    /**
     * 订单金额
     */
    @ApiModelProperty(value = "订单金额", example = "")
    private Object orderMoney;

    /**
     * 服务费（给商家的）
     * 发布广告：服务费，商家挖矿：佣金奖励（由用户手续费兑付）
     */
    @ApiModelProperty(value = "服务费", example = "")
    private Object serviceMoney;

    /**
     * 服务费率
     * 发布广告：服务费率，商家挖矿：佣金奖励（由用户手续费兑付）
     */
    @ApiModelProperty(value = "服务费率", example = "")
    private Object serviceRate;

    /**
     * 付款账号信息
     */
    @ApiModelProperty(value = "付款账号信息", example = "")
    private String payMethodInfo;

    @ApiModelProperty(value = "", example = "")
    private String wechatNick;

    @ApiModelProperty(value = "", example = "")
    private Integer isMerchantsBuy;


    /**
     * 商家销售奖励状态 0：未发放，1已发放 ,2不发放
     */
    @ApiModelProperty(value = "", example = "")
    private Integer saleRewardStatus;

    /**
     * 是否被标记的订单
     */
    @ApiModelProperty(value = "", example = "")
    private Integer marked;
}