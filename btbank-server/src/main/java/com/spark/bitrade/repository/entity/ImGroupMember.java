package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * (ImGroupMember)表实体类
 *
 * @author yangch
 * @since 2020-01-20 10:59:34
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class ImGroupMember {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 群id
     */
    @ApiModelProperty(value = "群id", example = "")
    private String tid;

    /**
     * 用户acid
     */
    @ApiModelProperty(value = "用户acid", example = "")
    private String acid;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", example = "")
    private Long memberId;

    /**
     * 用户名或者昵称
     */
    @ApiModelProperty(value = "用户名或者昵称", example = "")
    private String nick;


}