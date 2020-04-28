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
 * 奖品表(TurntablePrize)表实体类
 *
 * @author biu
 * @since 2020-01-08 17:26:02
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "奖品表")
public class TurntablePrize implements Serializable {

    /**
     * ID
     */
    @TableId
    @ApiModelProperty(value = "ID", example = "")
    private Integer id;

    /**
     * 活动ID
     */
    @ApiModelProperty(value = "活动ID", example = "")
    private Integer actId;

    /**
     * 奖品名称
     */
    @ApiModelProperty(value = "奖品名称", example = "")
    private String name;

    /**
     * 奖品金额
     */
    @ApiModelProperty(value = "奖品金额", example = "")
    private Double amount;

    /**
     * 奖品数量
     */
    @ApiModelProperty(value = "奖品数量", example = "")
    private Integer total;

    /**
     * 奖品库存
     */
    @ApiModelProperty(value = "奖品库存", example = "")
    private Integer stock;

    /**
     * 中奖概率
     */
    @ApiModelProperty(value = "中奖概率", example = "")
    private Double rate;

    /**
     * 图片
     */
    @ApiModelProperty(value = "图片", example = "")
    private String image;

    /**
     * BT：BT, OBJECT： 实物, NONE：谢谢参与
     */
    @ApiModelProperty(value = "BT：BT, OBJECT： 实物, NONE：谢谢参与", example = "")
    private String type;

    /**
     * 每人中奖上限
     */
    @ApiModelProperty(value = "每人中奖上限", example = "")
    private Integer toplimit;

    /**
     * 优先级
     */
    @ApiModelProperty(value = "优先级", example = "")
    private Integer priority;

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