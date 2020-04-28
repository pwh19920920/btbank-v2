package com.spark.bitrade.repository.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 企业矿工表(EnterpriseMiner)表实体类
 *
 * @author biu
 * @since 2019-12-23 17:15:02
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "企业矿工表")
public class EnterpriseMiner implements Serializable {

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
     * 矿池余额
     */
    @ApiModelProperty(value = "矿池余额", example = "")
    private BigDecimal balance;

    /**
     * 已挖总额
     */
    @ApiModelProperty(value = "已挖总额", example = "")
    private BigDecimal outlaySum;

    /**
     * 累计收益
     */
    @ApiModelProperty(value = "累计收益", example = "")
    private BigDecimal rewardSum;

    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名", example = "")
    private String realName;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码", example = "")
    private String mobilePhone;

    /**
     * 邮箱地址
     */
    @ApiModelProperty(value = "邮箱地址", example = "")
    private String email;

    /**
     * 0：未删除, 1：已删除
     */
    @ApiModelProperty(value = "0：未删除, 1：已删除", example = "")
    private Integer deleted;

    /**
     * 0：不生效 1：生效中
     */
    @ApiModelProperty(value = "0：不生效 1：生效中", example = "")
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

    /**
     * 企业矿工秘钥
     */
    @ApiModelProperty(value = "企业矿工秘钥", example = "")
    private String enterpriseKey;

}