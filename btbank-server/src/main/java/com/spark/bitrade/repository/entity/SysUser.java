package com.spark.bitrade.repository.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 操作员表(SysUser)表实体类
 *
 * @author yangch
 * @since 2020-01-19 17:56:46
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "操作员表")
public class SysUser {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 登录用户名
     */
    @ApiModelProperty(value = "登录用户名", example = "")
    private String username;

    /**
     * 操作员姓名
     */
    @ApiModelProperty(value = "操作员姓名", example = "")
    private String realName;

    /**
     * 登录密码
     */
    @ApiModelProperty(value = "登录密码", example = "")
    private String password;

    /**
     * 密码加密填充值
     */
    @ApiModelProperty(value = "密码加密填充值", example = "")
    private String salt;

    /**
     * 用户类型 {1:管理员,2:操作员}
     */
    @ApiModelProperty(value = "用户类型 {1:管理员,2:操作员}", example = "")
    private Integer userType;

    /**
     * 电子邮件
     */
    @ApiModelProperty(value = "电子邮件", example = "")
    private String email;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码", example = "")
    private String mobileNo;

    /**
     * 组织ID
     */
    @ApiModelProperty(value = "组织ID", example = "")
    private Integer orgId;

    /**
     * 组织名称(冗余)
     */
    @ApiModelProperty(value = "组织名称(冗余)", example = "")
    private String orgName;

    /**
     * 最后修改时间
     */
    @ApiModelProperty(value = "最后修改时间", example = "")
    private Date lastModifyTime;

    /**
     * 密码过期时间
     */
    @ApiModelProperty(value = "密码过期时间", example = "")
    private Date expirationTime;

    /**
     * 解锁时间
     */
    @ApiModelProperty(value = "解锁时间", example = "")
    private Date unlockTime;

    /**
     * 是否登陆{1:未登陆,2:已登陆}
     */
    @ApiModelProperty(value = "是否登陆{1:未登陆,2:已登陆}", example = "")
    private Integer loginStatus;

    /**
     * 登录失败次数
     */
    @ApiModelProperty(value = "登录失败次数", example = "")
    private Integer loginFailTimes;

    /**
     * 登陆时间
     */
    @ApiModelProperty(value = "登陆时间", example = "")
    private Date loginTime;

    /**
     * 状态 {1:有效,2:过期,3:锁定,4:禁用}
     */
    @ApiModelProperty(value = "状态 {1:有效,2:过期,3:锁定,4:禁用}", example = "")
    private Integer status;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述", example = "")
    private String descn;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间", example = "")
    private Date updateTime;


}