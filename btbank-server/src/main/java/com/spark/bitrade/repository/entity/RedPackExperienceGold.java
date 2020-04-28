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
 * 红包体检金流水表(RedPackExperienceGold)表实体类
 *
 * @author daring5920
 * @since 2019-12-08 10:44:35
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "红包体检金流水表")
public class RedPackExperienceGold {

    /**
     * 流水id
     */
    @TableId
    @ApiModelProperty(value = "流水id", example = "")
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", example = "")
    private Long memberId;

    /**
     * 活动类型（1：锁仓，2：解锁）
     */
    @ApiModelProperty(value = "活动类型（1：锁仓，2：解锁）", example = "")
    private Integer activityType;

    /**
     * 解锁方式（1：新用户，2：参与挖矿，3：推荐矿工，4：挖矿收益）
     */
    @ApiModelProperty(value = "解锁方式（1：新用户，2：参与挖矿，3：推荐矿工，4：挖矿收益）", example = "")
    private Integer lockType;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量", example = "")
    private BigDecimal amount;

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

    /**
     * 下级会员id
     */
    @ApiModelProperty(value = "下级会员id", example = "")
    private Long childMemberId;

    /**
     * 矿池佣金流水id
     */
    @ApiModelProperty(value = "矿池佣金流水id", example = "")
    private Long minePoolId;

    /**
     * 挖矿资产流水id
     */
    @ApiModelProperty(value = "挖矿资产流水id", example = "")
    private Long miningId;


}