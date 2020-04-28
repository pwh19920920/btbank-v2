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
 * 参与记录详情表(TurntableInvolvedRecord)表实体类
 *
 * @author biu
 * @since 2020-01-08 17:25:48
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "参与记录详情表")
public class TurntableInvolvedRecord implements Serializable {

    /**
     * ID
     */
    @TableId
    @ApiModelProperty(value = "ID", example = "")
    private Long id;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID", example = "")
    private Long memberId;

    /**
     * 活动ID
     */
    @ApiModelProperty(value = "活动ID", example = "")
    private Integer actId;

    /**
     * 奖品ID
     */
    @ApiModelProperty(value = "奖品ID", example = "")
    private Integer prizeId;

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