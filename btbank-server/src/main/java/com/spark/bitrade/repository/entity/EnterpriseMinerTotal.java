package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 企业挖矿汇总表(EnterpriseMinerTotal)表实体类
 *
 * @author zyj
 * @since 2019-12-27 11:33:00
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "企业挖矿汇总表")
public class EnterpriseMinerTotal {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期", example = "")
    private Date time;

    /**
     * 企业矿池转入次数
     */
    @ApiModelProperty(value = "企业矿池转入次数", example = "")
    private Integer intoCount;

    /**
     * 企业矿池转入人数
     */
    @ApiModelProperty(value = "企业矿池转入人数", example = "")
    private Integer intoPeople;

    /**
     * 企业矿池转入总额
     */
    @ApiModelProperty(value = "企业矿池转入总额", example = "")
    private BigDecimal intoSum;

    /**
     * 企业矿池转出总额
     */
    @ApiModelProperty(value = "企业矿池转出总额", example = "")
    private BigDecimal sendSum;

    /**
     * 企业挖矿总额
     */
    @ApiModelProperty(value = "企业挖矿总额", example = "")
    private BigDecimal mineSum;

    /**
     * 企业挖矿佣金总额
     */
    @ApiModelProperty(value = "企业挖矿佣金总额", example = "")
    private BigDecimal rewardSum;

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