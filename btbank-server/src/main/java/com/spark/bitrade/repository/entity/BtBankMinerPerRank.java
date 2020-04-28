package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 有效矿工业绩排名统计(BtBankMinerPerRank)表实体类
 *
 * @author daring5920
 * @since 2020-03-18 15:58:04
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "有效矿工业绩排名统计")
public class BtBankMinerPerRank {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期", example = "")
    private String time;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", example = "")
    private Long memberId;

    /**
     * 有效直推人数
     */
    @ApiModelProperty(value = "有效直推人数", example = "")
    private Long recommended;

    /**
     * 业绩:会员和直推矿工的(可用+矿池+大宗挖矿数量(BT))
     */
    @ApiModelProperty(value = "业绩:会员和直推矿工的(可用+矿池+大宗挖矿数量(BT))", example = "")
    private BigDecimal money;

    /**
     * 创建时间（管理后台代码生成所需）
     */
    @ApiModelProperty(value = "创建时间（管理后台代码生成所需）", example = "")
    private Date createTime;

    /**
     * 修改时间（管理后台代码生成所需）
     */
    @ApiModelProperty(value = "修改时间（管理后台代码生成所需）", example = "")
    private Date updateTime;


}