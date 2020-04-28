package com.spark.bitrade.repository.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 换汇币种配置(ForeignCurrency)表实体类
 *
 * @author yangch
 * @since 2020-02-04 11:47:27
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "换汇币种配置")
public class ForeignCurrency {

    /**
     * 主键
     */
    @TableId
    @ApiModelProperty(value = "主键", example = "")
    private Integer id;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种", example = "")
    private String currency;

    /**
     * 币种显示位置
     */
    @ApiModelProperty(value = "币种显示位置", example = "")
    private Integer location;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date updateTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    private Date createTime;

    /**
     * 图片地址
     */
    @ApiModelProperty(value = "图片地址", example = "")
    private String image;
    /**
     * 是否可用
     */
    @ApiModelProperty(value = "是否可用", example = "")
    private Integer status;
    /**
     * 币种中文
     */
    @ApiModelProperty(value = "币种中文", example = "")
    private String cnmane;

    /**
     * 图片完整路径
     */
    @ApiModelProperty(value = "币种中文", example = "")
    private String imageUrl;
}