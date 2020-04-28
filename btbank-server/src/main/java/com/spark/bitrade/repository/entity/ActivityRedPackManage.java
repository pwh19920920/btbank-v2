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
 * (ActivityRedPackManage)表实体类
 *
 * @author yangch
 * @since 2020-01-13 10:39:01
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class ActivityRedPackManage {

    /**
     * 活动id
     */
    @TableId
    @ApiModelProperty(value = "活动id", example = "")
    private Long id;

    /**
     * 活动名称
     */
    @ApiModelProperty(value = "活动名称", example = "")
    private String redpackName;

    /**
     * 活动开始时间
     */
    @ApiModelProperty(value = "活动开始时间", example = "")
    private Date startTime;

    /**
     * 活动结束时间
     */
    @ApiModelProperty(value = "活动结束时间", example = "")
    private Date endTime;

    /**
     * 活动币种
     */
    @ApiModelProperty(value = "活动币种", example = "")
    private String unit;

    /**
     * 发放总金额
     */
    @ApiModelProperty(value = "发放总金额", example = "")
    private BigDecimal totalAmount;

    /**
     * 剩余金额
     */
    @ApiModelProperty(value = "剩余金额", example = "")
    private BigDecimal surplusAmount;

    /**
     * 领取模式{1:随机数量,2:固定数量}
     */
    @ApiModelProperty(value = "领取模式{1:随机数量,2:固定数量}", example = "")
    private Short receiveType;

    /**
     * 红包金额最小值
     */
    @ApiModelProperty(value = "红包金额最小值", example = "")
    private BigDecimal minAmount;

    /**
     * 红包金额最大值
     */
    @ApiModelProperty(value = "红包金额最大值", example = "")
    private BigDecimal maxAmount;

    /**
     * 剩余数量
     */
    @ApiModelProperty(value = "剩余数量", example = "")
    private BigDecimal redPacketBalance;

    /**
     * 红包时限小时
     */
    @ApiModelProperty(value = "红包时限小时", example = "")
    private Short within;

    /**
     * 限用户类型参与活动{0:所有会员,1:新会员, 2:老会员}
     */
    @ApiModelProperty(value = "限用户类型参与活动{0:所有会员,1:新会员, 2:老会员}", example = "")
    private Short memberType;

    /**
     * 首页弹出优先级
     */
    @ApiModelProperty(value = "首页弹出优先级", example = "")
    private Integer priority;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    /**
     * 创建用户id
     */
    @ApiModelProperty(value = "创建用户id", example = "")
    private Long createUserid;

    /**
     * 修改用户
     */
    @ApiModelProperty(value = "修改用户", example = "")
    private Long updateUserid;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间", example = "")
    private Date updateTime;

    /**
     * 是否删除
     */
    @ApiModelProperty(value = "是否删除", example = "")
    private Object deleteFlag;

    /**
     * 红包图片路径
     */
    @ApiModelProperty(value = "红包图片路径", example = "")
    private String url;

    /**
     * 剩余份数
     */
    @ApiModelProperty(value = "剩余份数", example = "")
    private Integer surplusCount;

    /**
     * 发放类型 0锁仓发放 1直接发放
     */
    @ApiModelProperty(value = "发放类型 0锁仓发放 1直接发放", example = "")
    private Short releaseType;

    /**
     * 触发事件 0抢单挖矿 1推荐有效矿工
     */
    @ApiModelProperty(value = "触发事件 0抢单挖矿 1推荐有效矿工", example = "")
    private Short triggerEvent;
    /**
     * 触发次数
     */
    @ApiModelProperty(value = "当前次", example = "")
    private Integer triggerEventCount;
    /**
     * 当前次数
     */
    @ApiModelProperty(value = "触发事件 0抢单挖矿 1推荐有效矿工", example = "")
    private Integer triggerEventCurrent;
}