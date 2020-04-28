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
 * 中奖记录(TurntableWinning)表实体类
 *
 * @author biu
 * @since 2020-01-08 17:26:08
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "中奖记录")
public class TurntableWinning implements Serializable {

    /**
     * ID
     */
    @TableId
    @ApiModelProperty(value = "ID", example = "")
    private Long id;

    /**
     * 活动ID
     */
    @ApiModelProperty(value = "活动ID", example = "")
    private Integer actId;

    /**
     * 奖品ID
     */
    @ApiModelProperty(value = "奖品ID", example = "")
    private Integer prizeId;

    /**
     * 奖品名称
     */
    @ApiModelProperty(value = "奖品名称", example = "")
    private String prizeName;

    /**
     * 奖品金额
     */
    @ApiModelProperty(value = "奖品金额", example = "")
    private Double prizeAmount;

    /**
     * 奖品图片
     */
    @ApiModelProperty(value = "奖品图片", example = "")
    private String prizeImage;

    /**
     * 奖品类型
     */
    @ApiModelProperty(value = "奖品类型", example = "")
    private String prizeType;

    /**
     * 奖品位置
     */
    @ApiModelProperty(value = "奖品位置", example = "")
    private Integer priority;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", example = "")
    private Long memberId;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称", example = "")
    private String username;

    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名", example = "")
    private String realName;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号", example = "")
    private String mobilePhone;

    /**
     * 收货人姓名
     */
    @ApiModelProperty(value = "收货人姓名", example = "")
    private String contactName;

    /**
     * 收货人电话
     */
    @ApiModelProperty(value = "收货人电话", example = "")
    private String contactPhone;

    /**
     * 0：未发放 1：已发放 2：已完成
     */
    @ApiModelProperty(value = "0：未发放 1：已发放 2：已完成", example = "")
    private Integer state;

    /**
     * 发放时间
     */
    @ApiModelProperty(value = "发放时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date givingOutTime;

    /**
     * 操作人ID
     */
    @ApiModelProperty(value = "操作人ID", example = "")
    private Long operatorId;

    /**
     * 操作人姓名
     */
    @ApiModelProperty(value = "操作人姓名", example = "")
    private String operatorName;

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