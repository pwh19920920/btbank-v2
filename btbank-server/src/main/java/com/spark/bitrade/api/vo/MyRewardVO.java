package com.spark.bitrade.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 我的奖励vo
 */
@Data
@ApiModel("我的奖励")
public class MyRewardVO {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("数量")
    private BigDecimal amount;

    @ApiModelProperty("用户id")
    private Long memberId;

    @ApiModelProperty("发放时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("关联id")
    private String refId;

    @ApiModelProperty("奖励类型")
    private String comment;

    @ApiModelProperty("邀请关系（0无，1直接邀请，2间接邀请）")
    private Integer isSub;

    @ApiModelProperty("被推荐账号")
    private String username;

}
