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
 * 红包锁仓表(RedPackLock)表实体类
 *
 * @author daring5920
 * @since 2019-12-08 10:44:37
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "红包锁仓表")
public class RedPackLock {

    /**
     * 红包锁仓id
     */
    @TableId
    @ApiModelProperty(value = "红包锁仓id", example = "")
    private Long id;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id", example = "")
    private Long memberId;

    /**
     * 红包锁仓数量
     */
    @ApiModelProperty(value = "红包锁仓数量", example = "")
    private BigDecimal lockAmount;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间", example = "")
    private Date updateTime;


}