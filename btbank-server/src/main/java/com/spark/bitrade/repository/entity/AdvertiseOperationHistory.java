package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 广告操作记录表(AdvertiseOperationHistory)表实体类
 *
 * @author daring5920
 * @since 2019-11-27 17:53:20
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "广告操作记录表")
public class AdvertiseOperationHistory {

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "自增ID", example = "")
    private Long id;

    /**
     * 修改前ID 广告上下架状态:0=上架/1=下架/2=已关闭（删除）
     */
    @ApiModelProperty(value = "修改前ID 广告上下架状态:0=上架/1=下架/2=已关闭（删除）", example = "")
    private Integer oldStatus;

    /**
     * 新状态 广告上下架状态:0=上架/1=下架/2=已关闭（删除）
     */
    @ApiModelProperty(value = "新状态 广告上下架状态:0=上架/1=下架/2=已关闭（删除）", example = "")
    private Integer newStatus;

    /**
     * 对应的广告ID
     */
    @ApiModelProperty(value = "对应的广告ID", example = "")
    private Long advertiseId;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    private Date updateTime;


    /**
     * 广告类型
     */
    @ApiModelProperty(value = "广告类型", example = "")
    private Integer advertiseType;


    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", example = "")
    private Long memberId;


}