package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 增值福利参与资格(WelfareIncrQualification)表实体类
 *
 * @author biu
 * @since 2020-04-08 14:16:33
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "增值福利参与资格")
public class WelfareIncrQualification implements Serializable {

    /**
     * 会员 member_id
     */
    @TableId(type = IdType.NONE)
    @ApiModelProperty(value = " 会员 member_id", example = "")
    private Long id;

    /**
     * 获得次数
     */
    @ApiModelProperty(value = "获得次数", example = "")
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