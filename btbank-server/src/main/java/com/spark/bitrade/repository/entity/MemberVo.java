package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MemberVo {
    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long memberId;
    @TableId
    @ApiModelProperty(value = "im用户名称", example = "")
    private String username;
    @TableId
    @ApiModelProperty(value = "", example = "")
    private String realName;
    private String email;
    private String avatar;

    private String type;
    /**
     * 网易云通信ID可以指定登录token值，最大长度128字符
     */
    @ApiModelProperty(value = "网易云通信ID可以指定登录token值，最大长度128字符", example = "")
    private String token;
    /**
     * 网易云通信ID，最大长度32字符，必须保证一个
     */
    @ApiModelProperty(value = "网易云通信ID", example = "")
    private String accid;
    /**
     * 用户类型1矿工，2后台用户客服
     */
    @ApiModelProperty(value = "用户类型1矿工，2后台用户客服", example = "")
    private Integer userType;


    /**
     * 用户性别，0表示未知，1表示男，2女表示女，其它会报参数错误
     */
    @ApiModelProperty(value = "用户性别，0表示未知，1表示男，2女表示女，其它会报参数错误", example = "")
    private Integer gender;

    /**
     * 网易云通信ID头像URL，开发者可选填，最大长度1024

     */
    @ApiModelProperty(value = "网易云通信ID头像URL，开发者可选填，最大长度1024 ", example = "")
    private String icon;

    /**
     * 创建者acid
     */
    @ApiModelProperty(value = "创建者acid", example = "")
    private String creator;

    /**
     * 聊天室名称
     */
    @ApiModelProperty(value = "聊天室名称", example = "")
    private String name;

    /**
     * 公告
     */
    @ApiModelProperty(value = "公告", example = "")
    private String announcement;
    /**
     * 聊天室ID
     */
    @ApiModelProperty(value = "聊天室ID", example = "")
    private Long roomId;
}
