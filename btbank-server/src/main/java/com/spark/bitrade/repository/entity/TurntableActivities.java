package com.spark.bitrade.repository.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 活动表(TurntableActivities)表实体类
 *
 * @author biu
 * @since 2020-01-08 13:56:07
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "活动表")
public class TurntableActivities implements Serializable {

    /**
     * ID
     */
    @TableId
    @ApiModelProperty(value = "ID", example = "")
    private Integer id;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 该状态由活动时间决定；0：未开始或进行中 1：已完成
     */
    @ApiModelProperty(value = "该状态由活动时间决定；0：未开始或进行中 1：已完成", example = "")
    private Integer state;

    /**
     * 操作用户ID
     */
    @ApiModelProperty(value = "操作用户ID", example = "")
    private Long operatorId;

    /**
     * 操作用户名称
     */
    @ApiModelProperty(value = "操作用户名称", example = "")
    private String operatorName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;


}