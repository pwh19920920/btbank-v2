package com.spark.bitrade.repository.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 外汇线下换汇地址(ForeignCashLocation)表实体类
 *
 * @author yangch
 * @since 2020-02-04 11:35:35
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "外汇线下换汇地址")
public class ForeignCashLocation {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Integer id;

    /**
     * 地址
     */
    @ApiModelProperty(value = "地址", example = "")
    private String location;

    @ApiModelProperty(value = "备注", example = "")
    private String mark;

    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    @ApiModelProperty(value = "修改时间", example = "")
    private Date updateTime;
    @ApiModelProperty(value = "状态", example = "")
    private Integer status;


}