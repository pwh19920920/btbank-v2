package com.spark.bitrade.repository.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 会员操作记录表(BtBankMemberOperationRecord)表实体类
 *
 * @author daring5920
 * @since 2019-11-27 17:53:24
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "会员操作记录表")
public class BtBankMemberOperationRecord {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "ID", example = "")
    private Long id;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id", example = "")
    private Long memberId;

    /**
     * 会员类型{0:内部商家}
     */
    @ApiModelProperty(value = "会员类型{0:内部商家}", example = "")
    private Integer type;

    /**
     * 操作类型{0:增加,1:删除}
     */
    @ApiModelProperty(value = "操作类型{0:增加,1:删除}", example = "")
    private Integer operationType;

    /**
     * 操作人ID
     */
    @ApiModelProperty(value = "操作人ID", example = "")
    private Long operatorMemberId;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人", example = "")
    private String operatorMemberName;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    private Date updateTime;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "")
    private String remark;


}