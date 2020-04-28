package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户补贴记录表(OtcOrderSubsidyPerDay)表实体类
 *
 * @author daring5920
 * @since 2019-12-02 11:58:36
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "用户补贴记录表")
public class OtcOrderSubsidyPerDay {

    /**
     * id
     */
    @TableId
    @ApiModelProperty(value = "id", example = "")
    private Long id;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", example = "")
    private Long memberId;

    /**
     * 补贴时间
     */
    @ApiModelProperty(value = "补贴时间", example = "")
    private Date subsidyDate;

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
     * 补贴金额
     */
    @ApiModelProperty(value = "补贴金额", example = "")
    private BigDecimal amount;


}