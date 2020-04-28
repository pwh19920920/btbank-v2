package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 新人福利参与资格(WelfareNewQualification)表实体类
 *
 * @author biu
 * @since 2020-04-08 14:17:15
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "新人福利参与资格")
public class WelfareNewQualification implements Serializable {

    /**
     * member_id + 被推荐人id
     */
    @TableId
    @ApiModelProperty(value = "member_id + 被推荐人id", example = "")
    private String id;

    /**
     * 用户ID
     */
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
     * 0 标识无推荐人
     */
    @ApiModelProperty(value = "0 标识无推荐人", example = "")
    private Long subId;

    /**
     * 0：未使用 1：已使用;  2：失效 --> 仅当自己注册7天未使用会变为失效
     */
    @ApiModelProperty(value = "0：未使用 1：已使用;  2：失效 --> 仅当自己注册7天未使用会变为失效", example = "")
    private Integer status;

    /**
     * 关联明细记录ID
     */
    @ApiModelProperty(value = "关联明细记录ID", example = "")
    private String refId;

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