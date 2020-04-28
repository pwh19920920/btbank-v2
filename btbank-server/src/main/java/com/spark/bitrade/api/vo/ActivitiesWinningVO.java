package com.spark.bitrade.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spark.bitrade.repository.entity.TurntableWinning;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * ActivitiesWinningVO
 *
 * @author biu
 * @since 2020/1/7 17:04
 */
@Data
@ApiModel("中奖记录")
public class ActivitiesWinningVO {

    @ApiModelProperty("记录ID")
    private Long id;

    @ApiModelProperty("活动ID")
    private Integer actId;

    @ApiModelProperty("奖品名称")
    private String name;

    @ApiModelProperty("奖品图片")
    private String image;

    @ApiModelProperty("奖品类型")
    private String type;

    @ApiModelProperty("奖品图片")
    private String imageOss;

    @ApiModelProperty("记录状态 0：未发货，1：已发货，2：已完成")
    private Integer state;

    @ApiModelProperty("中奖时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

    public static ActivitiesWinningVO instanceOf(TurntableWinning win) {
        ActivitiesWinningVO vo = new ActivitiesWinningVO();

        vo.setId(win.getId());
        vo.setActId(win.getActId());
        vo.setName(win.getPrizeName());
        vo.setImage(win.getPrizeImage());
        vo.setType(win.getPrizeType());
        vo.setState(win.getState());
        vo.setTime(win.getCreateTime());

        return vo;
    }
}
