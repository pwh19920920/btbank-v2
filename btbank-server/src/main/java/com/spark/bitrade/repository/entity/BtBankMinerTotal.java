package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 挖矿汇总报表(BtBankMinerTotal)表实体类
 *
 * @author zyj
 * @since 2019-12-23 15:11:30
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "挖矿汇总报表")
public class BtBankMinerTotal {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期", example = "")
    private Date time;

    /**
     * 转入矿池次数
     */
    @ApiModelProperty(value = "转入矿池次数", example = "")
    private Integer count;

    /**
     * 转入矿池人数
     */
    @ApiModelProperty(value = "转入矿池人数", example = "")
    private Integer people;

    /**
     * 转入本金总额
     */
    @ApiModelProperty(value = "转入本金总额", example = "")
    private BigDecimal principal;

    /**
     * 结算佣金总额
     */
    @ApiModelProperty(value = "结算佣金总额", example = "")
    private BigDecimal reward;

    /**
     * 结算本金总额
     */
    @ApiModelProperty(value = "结算本金总额", example = "")
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