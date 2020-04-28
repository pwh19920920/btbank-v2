package com.spark.bitrade.repository.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * btbank规则配置(OtcConfigDataDict)表实体类
 *
 * @author daring5920
 * @since 2019-11-27 17:53:29
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "btbank规则配置")
public class OtcConfigDataDict {

    /**
     * 配置编号
     */
    @TableId(value = "dict_id", type = IdType.INPUT)
    @ApiModelProperty(value = "配置编号", example = "")
    private String dictId;
    /**
     * 配置KEY
     */
    @TableId
    @ApiModelProperty(value = "配置KEY", example = "")
    private String dictKey;

    /**
     * 配置VALUE
     */
    @ApiModelProperty(value = "配置VALUE", example = "")
    private String dictVal;

    /**
     * 数据类型
     */
    @ApiModelProperty(value = "数据类型", example = "")
    private String dictType;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述", example = "")
    private String remark;

    /**
     * 状态{0:失效,1:生效}
     */
    @ApiModelProperty(value = "状态{0:失效,1:生效}", example = "")
    private Integer status;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序", example = "")
    private Integer sort;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    private Date updateTime;

    /**
     * 添加时间
     */
    @ApiModelProperty(value = "添加时间", example = "")
    private Date createTime;


}