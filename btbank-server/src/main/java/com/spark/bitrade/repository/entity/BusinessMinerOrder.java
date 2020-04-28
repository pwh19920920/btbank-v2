package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商家矿池订单表(BusinessMinerOrder)表实体类
 *
 * @author daring5920
 * @since 2019-11-27 17:53:27
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "商家矿池订单表")
public class BusinessMinerOrder {

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 出售用户ID
     */
    @TableField(value = "sell_id")
    @ApiModelProperty(value = "出售用户ID", example = "")
    private Long sellId;

    /**
     * 购买用户ID
     */
    @TableField(value = "buy_id")
    @ApiModelProperty(value = "购买用户ID", example = "")
    private Long buyId;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量", example = "")
    private BigDecimal amount;

    /**
     * 手续费
     */
    @ApiModelProperty(value = "手续费", example = "")
    private BigDecimal fee;

    /**
     * 奖励金额
     */
    @TableField(value = "reward_amount")
    @ApiModelProperty(value = "奖励金额", example = "")
    private BigDecimal rewardAmount;

    /**
     * 订单状态{0:新订单,1:未付款,2:已付款,3:已完成,4:申诉中,5:已关闭}
     */
    @ApiModelProperty(value = "订单状态{0:新订单,1:未付款,2:已付款,3:已完成,4:申诉中,5:已关闭}", example = "")
    private Integer status;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value = "更新时间", example = "")
    private Date updateTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    /**
     * 关联id
     */
    @TableField(value = "ref_id")
    @ApiModelProperty(value = "关联id", example = "")
    private String refId;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "")
    private String remark;

    /**
     * 付款方式
     */
    @TableField(value = "pay_mode")
    @ApiModelProperty(value = "付款方式", example = "")
    private String payMode;

    /**
     * 订单标记次数
     */
    @TableField(value = "mark_count")
    @ApiModelProperty(value = "订单标记次数", example = "")
    private Integer markCount;

    /**
     * 标记过这个订单得用户
     */
    @TableField(value = "mark_member")
    @ApiModelProperty(value = "标记过这个订单得用户", example = "")
    private String markMember;

    @TableField(value = "queue_status")
    @ApiModelProperty(value = "排队状态", example = "")
    private Integer queueStatus;
}