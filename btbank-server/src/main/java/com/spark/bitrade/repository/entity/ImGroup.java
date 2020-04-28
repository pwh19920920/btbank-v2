package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * (ImGroup)表实体类
 *
 * @author yangch
 * @since 2020-01-20 10:58:22
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class ImGroup {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 群主用户帐号
     */
    @ApiModelProperty(value = "群主用户帐号", example = "")
    private String ownerAcid;

    /**
     * 群名称
     */
    @ApiModelProperty(value = "群名称", example = "")
    private String tname;

    /**
     * 群描述
     */
    @ApiModelProperty(value = "群描述", example = "")
    private String intro;

    /**
     * 群公告
     */
    @ApiModelProperty(value = "群公告", example = "")
    private String announcement;

    /**
     * 群头像，最大长度1024字符
     */
    @ApiModelProperty(value = "群头像，最大长度1024字符", example = "")
    private String icon;

    /**
     * 云信服务器产生，群唯一标识，创建群时会返回，最大长度128字符
     */
    @ApiModelProperty(value = "云信服务器产生，群唯一标识，创建群时会返回，最大长度128字符", example = "")
    private String tid;
    /**
     * 当前群人数
     */
    @ApiModelProperty(value = "当前群人数", example = "")
    private int groupSize;



}