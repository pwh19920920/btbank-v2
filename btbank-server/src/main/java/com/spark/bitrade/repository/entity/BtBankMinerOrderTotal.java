package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 矿池订单汇总报表(BtBankMinerOrderTotal)表实体类
 *
 * @author zyj
 * @since 2019-12-16 14:55:03
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "矿池订单汇总报表")
public class BtBankMinerOrderTotal {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期", example = "")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date time;

    /**
     * 抢单人数
     */
    @ApiModelProperty(value = "抢单人数", example = "")
    private Integer grabpeople;

    /**
     * 抢单次数
     */
    @ApiModelProperty(value = "抢单次数", example = "")
    private Integer grabCount;

    /**
     * 抢单总额
     */
    @ApiModelProperty(value = "抢单总额", example = "")
    private BigDecimal grabSum;

    /**
     * 派单人数
     */
    @ApiModelProperty(value = "派单人数", example = "")
    private Integer sendPeople;

    /**
     * 派单次数
     */
    @ApiModelProperty(value = "派单次数", example = "")
    private Integer sendCount;

    /**
     * 派单总额
     */
    @ApiModelProperty(value = "派单总额", example = "")
    private BigDecimal sendSum;

    /**
     * 固定收益人数
     */
    @ApiModelProperty(value = "固定收益人数", example = "")
    private Integer fixPeople;

    /**
     * 固定收益次数
     */
    @ApiModelProperty(value = "固定收益次数", example = "")
    private Integer fixCount;

    /**
     * 固定收益总额
     */
    @ApiModelProperty(value = "固定收益总额", example = "")
    private BigDecimal fixSum;


    /**
     * 当日矿池总额
     */
    @ApiModelProperty(value = "当日矿池总额", example = "")
    private BigDecimal needUnlockTotalAmount;

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