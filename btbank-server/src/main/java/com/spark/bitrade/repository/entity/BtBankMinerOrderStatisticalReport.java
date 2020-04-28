package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@ApiModel(value="com-spark-bitrade-repository-entity-BtBankMinerOrderStatisticalReport")
@Data
@TableName(value = "bt_bank_miner_order_statistical_report")
public class BtBankMinerOrderStatisticalReport implements Serializable {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value="自增ID")
    private Long id;

    /**
     * 报告结算日期
     */
    @TableField(value = "report_date")
    @ApiModelProperty(value="报告结算日期")
    private Date reportDate;

    /**
     * 当日抢单解锁用户数
     */
    @TableField(value = "grab_member_num")
    @ApiModelProperty(value="当日抢单解锁用户数")
    private Integer grabMemberNum;

    /**
     * 当日抢单解锁笔数
     */
    @TableField(value = "grab_times")
    @ApiModelProperty(value="当日抢单解锁笔数")
    private Integer grabTimes;

    /**
     * 当日抢单解锁总额
     */
    @TableField(value = "grab_total_amount")
    @ApiModelProperty(value="当日抢单解锁总额")
    private BigDecimal grabTotalAmount;

    /**
     * 当日派单解锁用户数
     */
    @TableField(value = "dispatch_member_num")
    @ApiModelProperty(value="当日派单解锁用户数")
    private Integer dispatchMemberNum;

    /**
     * 当日派单解锁次
     */
    @TableField(value = "dispatch_times")
    @ApiModelProperty(value="当日派单解锁次")
    private Integer dispatchTimes;

    /**
     * 当日派单解锁总额
     */
    @TableField(value = "dispatch_total_amount")
    @ApiModelProperty(value="当日派单解锁总额")
    private BigDecimal dispatchTotalAmount;

    /**
     * 当日固定解锁用户数
     */
    @TableField(value = "fixed_member_num")
    @ApiModelProperty(value="当日固定解锁用户数")
    private Integer fixedMemberNum;

    /**
     * 当日固定解锁次数
     */
    @TableField(value = "fixed_times")
    @ApiModelProperty(value="当日固定解锁次数")
    private Integer fixedTimes;

    /**
     * 当日固定本金解锁总额
     */
    @TableField(value = "fixed_totoal_amount")
    @ApiModelProperty(value="当日固定本金解锁总额")
    private Integer fixedTotoalAmount;

    /**
     * 当日矿池总额
     */
    @TableField(value = "miner_pool_totoal_amount")
    @ApiModelProperty(value="当日矿池总额")
    private BigDecimal minerPoolTotoalAmount;

    /**
     * 当日需要解锁总额
     */
    @TableField(value = "need_unlock_total_amount")
    @ApiModelProperty(value="当日需要解锁总额")
    private BigDecimal needUnlockTotalAmount;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value="创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value="更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}