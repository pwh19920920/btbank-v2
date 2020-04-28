package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * BtAppeal
 *
 * @author biu
 * @since 2019/12/1 18:50
 */
@Data
@ApiModel("申诉")
@TableName(value = "appeal")
public class BtAppeal {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "ID")
    private Long id;

    @TableField(value = "initiator_id")
    @ApiModelProperty(value = "发起人")
    private Long initiatorId;

    @TableField(value = "associate_id")
    @ApiModelProperty(value = "关联人")
    private Long associateId;


    @TableField(value = "is_success")
    @ApiModelProperty(value = "是否胜诉 {1:是, 0:否}")
    private Integer isSuccess;

    @TableField(value = "order_id")
    @ApiModelProperty(value = "订单id")
    private Long orderId;

    @TableField(value = "appeal_type")
    @ApiModelProperty(value = "申诉类型 0请求放币，1请求取消订单，2其他,3商家挖矿订单")
    private Integer appealType;

    @TableField(value = "remark")
    @ApiModelProperty(value = "描述")
    private String remark;

    @TableField(value = "status")
    @ApiModelProperty(value = "状态")
    private Integer status;

    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
