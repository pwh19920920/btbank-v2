package com.spark.bitrade.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spark.bitrade.repository.entity.TurntableActivities;
import com.spark.bitrade.repository.entity.TurntablePrize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * ActivitiesVO
 *
 * @author biu
 * @since 2020/1/7 16:54
 */
@Data
@ApiModel("抽奖活动")
public class ActivitiesVO {

    @ApiModelProperty("活动ID")
    private Integer id;

    @ApiModelProperty("活动奖品")
    private List<ActivitiesPrizeVO> prizes;

    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    public void peek(Consumer<ActivitiesPrizeVO> func) {
        if (prizes != null) {
            for (ActivitiesPrizeVO prize : prizes) {
                func.accept(prize);
            }
        }
    }

    public static ActivitiesVO instanceOf(TurntableActivities activities, List<TurntablePrize> prizes) {
        ActivitiesVO vo = new ActivitiesVO();

        vo.setId(activities.getId());
        vo.setPrizes(ActivitiesPrizeVO.listOf(prizes));
        vo.setStartTime(activities.getStartTime());
        vo.setEndTime(activities.getEndTime());
        return vo;
    }
}
