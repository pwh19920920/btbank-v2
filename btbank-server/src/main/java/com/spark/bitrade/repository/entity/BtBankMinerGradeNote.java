package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@ApiModel(value = "com-spark-bitrade-repository-entity-BtBankMinerGradeNote")
@Data
@TableName(value = "bt_bank_miner_grade_note")
public class BtBankMinerGradeNote implements Serializable {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "ID")
    private Long id;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 用户Id
     */
    @TableField(value = "member_id")
    @ApiModelProperty(value = "用户Id")
    private Long memberId;

    /**
     * 是否已处理 0未处理，1 已处理
     */
    @TableField(value = "is_operation")
    @ApiModelProperty(value = "是否已处理 0未处理，1 已处理")
    private Boolean isOperation;

    /**
     * 0:默认未处理 1：金牌矿工申请失败，2：金牌矿工申请成功3：手动设置金牌矿工 4：手动取消金牌矿工
     */
    @TableField(value = "type")
    @ApiModelProperty(value = "0:默认未处理 1：金牌矿工申请失败，2：金牌矿工申请成功3：手动设置金牌矿工 4：手动取消金牌矿工")
    private Integer type;

    /**
     * 操作人ID
     */
    @TableField(value = "operator_member_id")
    @ApiModelProperty(value = "操作人ID")
    private Long operatorMemberId;

    /**
     * 操作人
     */
    @TableField(value = "operator_member_name")
    @ApiModelProperty(value = "操作人")
    private String operatorMemberName;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    private static final long serialVersionUID = 1L;
}