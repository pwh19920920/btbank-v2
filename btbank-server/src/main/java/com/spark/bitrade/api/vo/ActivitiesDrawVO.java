package com.spark.bitrade.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ActivitiesDrawVO
 *
 * @author biu
 * @since 2020/1/7 17:00
 */
@Data
@ApiModel("抽奖结果")
public class ActivitiesDrawVO {

    @ApiModelProperty("活动ID")
    private Integer actId;

    @ApiModelProperty("中奖记录ID,未中奖 = 0")
    private Long winId;

    @ApiModelProperty("奖品位置")
    private Integer priority;

    public static ActivitiesDrawVO getInstance(Integer actId, Long winId, Integer priority) {
        ActivitiesDrawVO vo = new ActivitiesDrawVO();
        vo.setActId(actId);
        vo.setWinId(winId);
        vo.setPriority(priority);
        return vo;
    }
}
