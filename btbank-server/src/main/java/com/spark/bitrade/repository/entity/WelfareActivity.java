package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 福利包活动(WelfareActivity)表实体类
 *
 * @author biu
 * @since 2020-04-08 21:05:58
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "福利包活动")
public class WelfareActivity implements Serializable {

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "ID", example = "")
    private Integer id;

    /**
     * eg. 新人福利挖矿第一期活动
     */
    @ApiModelProperty(value = "eg. 新人福利挖矿第一期活动", example = "")
    private String name;

    /**
     * 0:新人福利包 1:增值福利包
     */
    @ApiModelProperty(value = "0:新人福利包 1:增值福利包", example = "")
    private Integer type;

    /**
     * 默认1递增
     */
    @ApiModelProperty(value = " 默认1递增", example = "")
    private Integer period;

    /**
     * 13天12小时转换为秒=1166400
     */
    @ApiModelProperty(value = "13天12小时转换为秒=1166400", example = "")
    private Long lockTime;

    /**
     * 固定7.5%=0.075
     */
    @ApiModelProperty(value = "固定7.5%=0.075", example = "")
    private BigDecimal earningRate;

    /**
     * 当天上午10:00
     */
    @ApiModelProperty(value = "当天上午10:00", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date openningTime;

    /**
     * 当天晚上22:00
     */
    @ApiModelProperty(value = "当天晚上22:00", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date closingTime;

    /**
     * 封盘时间往后推13天12小时，统一释放
     */
    @ApiModelProperty(value = "封盘时间往后推13天12小时，统一释放", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date releaseTime;

    /**
     * 每份固定1万BT=10000
     */
    @ApiModelProperty(value = "每份固定1万BT=10000", example = "")
    private BigDecimal amount;

    /**
     * 福利包描述内容
     */
    @ApiModelProperty(value = "福利包描述内容", example = "")
    private String remark;

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