package com.spark.bitrade.repository.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 企业矿工申请表(EnterpriseMinerApplication)表实体类
 *
 * @author biu
 * @since 2019-12-24 16:34:59
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "企业矿工申请表")
public class EnterpriseMinerApplication implements Serializable {

    /**
     * ID
     */
    @TableId
    @ApiModelProperty(value = "ID", example = "")
    private Integer id;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID", example = "")
    private Long memberId;

    /**
     * 申请类型 1：加入申请 2：退出申请
     */
    @ApiModelProperty(value = "申请类型 1：加入申请 2：退出申请", example = "")
    private Integer type;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", example = "")
    private String realName;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码", example = "")
    private String mobilePhone;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱", example = "")
    private String email;

    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号", example = "")
    private String idCard;

    /**
     * 身份证正面
     */
    @ApiModelProperty(value = "身份证正面", example = "")
    private String idCardFront;

    /**
     * 身份证背面
     */
    @ApiModelProperty(value = "身份证背面", example = "")
    private String idCardBack;

    /**
     * 手持身份证
     */
    @ApiModelProperty(value = "手持身份证", example = "")
    private String idCardInHand;

    /**
     * 营业执照
     */
    @ApiModelProperty(value = "营业执照", example = "")
    private String businessLicense;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述", example = "")
    private String description;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "")
    private String remark;

    /**
     * 审批人
     */
    @ApiModelProperty(value = "审批人", example = "")
    private Long approverId;

    /**
     * 审批人真实姓名
     */
    @ApiModelProperty(value = "审批人真实姓名", example = "")
    private String approverRealName;

    /**
     * 0：待审核 1：已通过 2：已拒绝
     */
    @ApiModelProperty(value = "0：待审核 1：已通过 2：已拒绝", example = "")
    private Integer status;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;


}