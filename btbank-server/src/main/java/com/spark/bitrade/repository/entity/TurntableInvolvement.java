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
 * 参与记录表(TurntableInvolvement)表实体类
 *
 * @author biu
 * @since 2020-01-08 13:56:22
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "参与记录表")
public class TurntableInvolvement implements Serializable {

    /**
     * 会员ID
     */
    @TableId
    @ApiModelProperty(value = "会员ID", example = "")
    private Long id;

    /**
     * 次数总计
     */
    @ApiModelProperty(value = "次数总计", example = "")
    private Integer total;

    /**
     * 剩余次数
     */
    @ApiModelProperty(value = "剩余次数", example = "")
    private Integer surplus;

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