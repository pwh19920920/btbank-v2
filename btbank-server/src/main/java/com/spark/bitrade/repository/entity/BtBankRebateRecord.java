package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@ApiModel(value = "com-spark-bitrade-repository-entity-BtBankRebateRecord")
@Data
@TableName(value = "bt_bank_rebate_record")
public class BtBankRebateRecord implements Serializable {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "ID")
    private Long id;

    /**
     * 产生奖励动作的矿工用户ID
     */
    @TableField(value = "miner_member_id")
    @ApiModelProperty(value = "产生奖励动作的矿工用户ID")
    private Long minerMemberId;

    /**
     * 奖励来源释放记录ID
     */
    @TableField(value = "miner_balance_transaction_id")
    @ApiModelProperty(value = "奖励来源释放记录ID")
    private Long minerBalanceTransactionId;

    /**
     * 挖矿奖励类型：4 抢单挖矿奖励，7 派单挖矿奖励，9 固定收益挖矿奖励
     */
    @TableField(value = "miner_reward_type")
    @ApiModelProperty(value = "挖矿奖励类型：4 抢单挖矿奖励，7 派单挖矿奖励，9 固定收益挖矿奖励 13大宗挖矿 14新人福利挖矿 15增值福利挖矿")
    private Integer minerRewardType;

    /**
     * 奖励来源订单获得的奖励金额
     */
    @TableField(value = "reward_amount")
    @ApiModelProperty(value = "奖励来源订单获得的奖励金额")
    private BigDecimal rewardAmount;

    /**
     * 获得奖励的矿工ID
     */
    @TableField(value = "rebate_member_id")
    @ApiModelProperty(value = "获得奖励的矿工ID")
    private Long rebateMemberId;

    /**
     * 奖励类型：0 直推奖励，1 金牌矿工奖励
     */
    @TableField(value = "rebate_type")
    @ApiModelProperty(value = "奖励类型：0 直推奖励，1 金牌矿工奖励")
    private Integer rebateType;

    /**
     * 返佣奖励金额
     */
    @TableField(value = "rebate_amount")
    @ApiModelProperty(value = "返佣奖励金额")
    private BigDecimal rebateAmount;

    /**
     * 关联奖励资产来源
     */
    @TableField(value = "ref_id")
    @ApiModelProperty(value = "关联奖励资产来源")
    private Long refId;

    /**
     * 推荐层级，miner_member自己算0，rebate_member是直属推荐上级算1
     */
    @TableField(value = "rebate_level")
    @ApiModelProperty(value = "推荐层级，miner_member自己算0，rebate_member是直属推荐上级算1")
    private Integer rebateLevel;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}