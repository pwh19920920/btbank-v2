package com.spark.bitrade.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * (BtBankMinerInfo)表实体类
 *
 * @author daring5920
 * @since 2019-11-10 19:32:50
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class BtBankMinerInfo {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 旷工等级
     */
    @ApiModelProperty(value = "旷工等级", example = "")
    private Integer minerGrade;

    /**
     * 用新申请的用户等级
     */
    @ApiModelProperty(value = "用新申请的用户等级", example = "")
    private Integer minerNewGradeApply;

    /**
     * 关联的用户ID
     */
    @ApiModelProperty(value = "关联的用户ID", example = "")
    private Long memberId;


}