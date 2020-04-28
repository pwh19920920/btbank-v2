package com.spark.bitrade.entity.chat;


import lombok.Data;
import lombok.ToString;

/**
 * (ImMember)表实体类
 *
 * @author yangch
 * @since 2020-01-19 14:47:02
 */
@Data
@ToString(callSuper = true)
public class ImMember {
    private Long id;

    private Long memberId;

    /**
     * 网易云通信ID，最大长度32字符，必须保证一个
     */
    private String accid;

    /**
     * 网易云通信ID昵称，最大长度64字符。
     */
    private String name;

    /**
     * json属性，开发者可选填，最大长度1024字符
     */
    private String props;

    /**
     * 网易云通信ID头像URL，开发者可选填，最大长度1024

     */
    private String icon;

    /**
     * 网易云通信ID可以指定登录token值，最大长度128字符
     */
    private String token;

    /**
     * 用户签名，最大长度256
     */
    private String sign;

    /**
     * 用户email，最大长度64字符
     */
    private String email;

    /**
     * 用户生日，最大长度16
     */
    private String birth;

    /**
     * 用户mobile，最大长度32字符，非中国大陆手机号码需要填写国家代码
     */
    private String mobile;

    /**
     * 用户性别，0表示未知，1表示男，2女表示女，其它会报参数错误
     */
    private Integer gender;

    /**
     * 用户名片扩展字段，最大长度1024字符
     */
    private String ex;

    /**
     * 用户类型1矿工，2后台用户客服
     */
    private Integer userType;

}