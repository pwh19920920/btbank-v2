package com.spark.bitrade.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.constant.BooleanEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * (MemberSecuritySet)表实体类
 *
 * @author wsy
 * @since 2019-06-14 14:16:08
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class MemberSecuritySet {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 是否开启google认证
     */
    @ApiModelProperty(value = "是否开启google认证", example = "")
    private String isOpenGoogle;

    /**
     * 是否开启手机认证
     */
    @ApiModelProperty(value = "是否开启手机认证", example = "")
    private String isOpenPhone;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id", example = "")
    private Long memberId;

    /**
     * 是否开启google登录认证
     */
    @ApiModelProperty(value = "是否开启google登录认证", example = "")
    private BooleanEnum isOpenGoogleLogin = BooleanEnum.IS_FALSE;

    /**
     * 是否开启google提币认证
     */
    @ApiModelProperty(value = "是否开启google提币认证", example = "")
    private BooleanEnum isOpenGoogleUpCoin = BooleanEnum.IS_FALSE;

    /**
     * 是否开启手机登录认证
     */
    @ApiModelProperty(value = "是否开启手机登录认证", example = "")
    private BooleanEnum isOpenPhoneLogin = BooleanEnum.IS_FALSE;

    /**
     * 是否开启手机提币认证
     */
    @ApiModelProperty(value = "是否开启手机提币认证", example = "")
    private BooleanEnum isOpenPhoneUpCoin = BooleanEnum.IS_FALSE;

    /**
     * 是否开启总资产显示
     */
    @ApiModelProperty(value = "是否开启总资产显示", example = "")
    private BooleanEnum isOpenPropertyShow = BooleanEnum.IS_FALSE;


}