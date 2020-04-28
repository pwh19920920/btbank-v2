package com.spark.bitrade.repository.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * (MemberAssetStatistics)表实体类
 *
 * @author daring5920
 * @since 2019-12-24 11:50:00
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class MemberAssetStatistics {

    /**
     * 用户ID
     */
    @TableId
    @ApiModelProperty(value = "用户ID", example = "")
    private Long memberId;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号", example = "")
    private String mobilePhone;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名", example = "")
    private String realName;

    /**
     * 矿工级别
     */
    @ApiModelProperty(value = "矿工级别", example = "")
    private String minerGrade;

    /**
     * 锁仓本金
     */
    @ApiModelProperty(value = "锁仓本金", example = "")
    private BigDecimal lockCoin;

    /**
     * 活期宝余额
     */
    @ApiModelProperty(value = "活期宝余额", example = "")
    private BigDecimal hqbBalance;

    /**
     * 矿池总额
     */
    @ApiModelProperty(value = "矿池总额", example = "")
    private BigDecimal bttotalBalance;

    /**
     * 直接推荐矿工人数
     */
    @ApiModelProperty(value = "直接推荐矿工人数", example = "")
    private Integer invitePeople;

    /**
     * 挖矿当日收益
     */
    @ApiModelProperty(value = "挖矿当日收益", example = "")
    private BigDecimal dayRewardSum;

    /**
     * 挖矿累计收益
     */
    @ApiModelProperty(value = "挖矿累计收益", example = "")
    private BigDecimal gotRewardSum;

    /**
     * 理财账户当日释放
     */
    @ApiModelProperty(value = "理财账户当日释放", example = "")
    private BigDecimal unlockCoinToday;

    private Date startTime;

    private Date endTime;

}