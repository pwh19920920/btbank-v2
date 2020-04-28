package com.spark.bitrade.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: Zhong Jiang
 * @date: 2020-03-23 12:11
 */

@ApiModel("群聊校验结果")
@Data
public class MinerImVo {

    private Long id;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id")
    private Long memberId;

    /**
     * 网易云通信ID，最大长度32字符，必须保证一个
     */
    @ApiModelProperty(value = "网易云通信ID")
    private String accid;

    /**
     * 用户类型1矿工，2后台用户客服
     */
    @ApiModelProperty(value = "用户类型1矿工，2后台用户客服")
    private Integer userType;

    @ApiModelProperty(value = "手机号码")
    private String mobile;

    private String realName;

    private String email;

    @ApiModelProperty(value = "公告", example = "")
    private String announcement;

    /**
     * 网易云通信ID头像URL，开发者可选填，最大长度1024
     */
    @ApiModelProperty(value = "网易云通信ID头像URL，开发者可选填，最大长度1024 ", example = "")
    private String icon;

    /**
     * 用户性别，0表示未知，1表示男，2女表示女，其它会报参数错误
     */
    @ApiModelProperty(value = "用户性别，0表示未知，1表示男，2女表示女，其它会报参数错误", example = "")
    private Integer gender;

    private String token;
}
