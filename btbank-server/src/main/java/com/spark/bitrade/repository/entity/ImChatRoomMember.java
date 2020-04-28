package com.spark.bitrade.repository.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * (ImChatRoomMember)表实体类
 *
 * @author yangch
 * @since 2020-01-20 14:51:20
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class ImChatRoomMember {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 创建者acid
     */
    @ApiModelProperty(value = "创建者acid", example = "")
    private String creator;

    /**
     * 聊天室名称
     */
    @ApiModelProperty(value = "聊天室名称", example = "")
    private String name;

    /**
     * 公告
     */
    @ApiModelProperty(value = "公告", example = "")
    private String announcement;

    /**
     * 直播地址
     */
    @ApiModelProperty(value = "直播地址", example = "")
    private String broadcasturl;

    /**
     * 扩展字段
     */
    @ApiModelProperty(value = "扩展字段", example = "")
    private String ext;

    /**
     * 队列管理权限0:所有人都有权限变更队列，1:只有主播管理员才能操作变更
     */
    @ApiModelProperty(value = "队列管理权限0:所有人都有权限变更队列，1:只有主播管理员才能操作变更", example = "")
    private Integer queuelevel;

    @ApiModelProperty(value = "", example = "")
    private Date createTime;

    @ApiModelProperty(value = "", example = "")
    private Date updateTime;


}