package com.spark.bitrade.repository.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 奖励金额配置(RankRewardConfig)表实体类
 *
 * @author daring5920
 * @since 2019-12-17 18:08:24
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "奖励金额配置")
public class RankRewardConfig {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 奖励类型
     */
    @ApiModelProperty(value = "奖励类型", example = "")
    private Integer rewardType;

    /**
     * 奖励金额
     */
    @ApiModelProperty(value = "奖励金额", example = "")
    private BigDecimal reward;

    /**
     * 奖励时间
     */
    @ApiModelProperty(value = "奖励时间", example = "")
    private Date rewardTime;

    /**
     * 修改前奖励金额奖励金额
     */
    @ApiModelProperty(value = "修改前奖励金额奖励金额", example = "")
    private BigDecimal oldreward;

    /**
     * 奖励等级
     */
    @ApiModelProperty(value = "奖励等级", example = "")
    private Integer rewardLevel;

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
     * 8、系统管理员
     */
    @ApiModelProperty(value = "8、系统管理员", example = "")
    private Long adminId;

    /**
     * 管理员真实姓名
     */
    @ApiModelProperty(value = "管理员真实姓名", example = "")
    private String adminRealName;


}