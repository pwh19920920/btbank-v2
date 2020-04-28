package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * (ImMember)表实体类
 *
 * @author yangch
 * @since 2020-01-19 14:47:02
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class ImMember {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    @ApiModelProperty(value = "", example = "")
    private Long memberId;

    /**
     * 网易云通信ID，最大长度32字符，必须保证一个
     */
    @ApiModelProperty(value = "网易云通信ID", example = "")
    private String accid;

    /**
     * 网易云通信ID昵称，最大长度64字符。
     */
    @ApiModelProperty(value = "网易云通信ID昵称，最大长度64字符。", example = "")
    private String name;

    /**
     * json属性，开发者可选填，最大长度1024字符
     */
    @ApiModelProperty(value = "json属性，开发者可选填，最大长度1024字符", example = "")
    private String props;

    /**
     * 网易云通信ID头像URL，开发者可选填，最大长度1024

     */
    @ApiModelProperty(value = "网易云通信ID头像URL，开发者可选填，最大长度1024 ", example = "")
    private String icon;

    /**
     * 网易云通信ID可以指定登录token值，最大长度128字符
     */
    @ApiModelProperty(value = "网易云通信ID可以指定登录token值，最大长度128字符", example = "")
    private String token;

    /**
     * 用户签名，最大长度256
     */
    @ApiModelProperty(value = "用户签名，最大长度256", example = "")
    private String sign;

    /**
     * 用户email，最大长度64字符
     */
    @ApiModelProperty(value = "用户email，最大长度64字符", example = "")
    private String email;

    /**
     * 用户生日，最大长度16
     */
    @ApiModelProperty(value = "用户生日，最大长度16", example = "")
    private String birth;

    /**
     * 用户mobile，最大长度32字符，非中国大陆手机号码需要填写国家代码
     */
    @ApiModelProperty(value = "用户mobile，最大长度32字符，非中国大陆手机号码需要填写国家代码", example = "")
    private String mobile;

    /**
     * 用户性别，0表示未知，1表示男，2女表示女，其它会报参数错误
     */
    @ApiModelProperty(value = "用户性别，0表示未知，1表示男，2女表示女，其它会报参数错误", example = "")
    private Integer gender;

    /**
     * 用户名片扩展字段，最大长度1024字符
     */
    @ApiModelProperty(value = "用户名片扩展字段，最大长度1024字符", example = "")
    private String ex;

    /**
     * 用户类型1矿工，2后台用户客服
     */
    @ApiModelProperty(value = "用户类型1矿工，2后台用户客服", example = "")
    private Integer userType;

}