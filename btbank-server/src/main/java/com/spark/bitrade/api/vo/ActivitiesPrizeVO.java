package com.spark.bitrade.api.vo;

import com.spark.bitrade.repository.entity.TurntablePrize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ActivitiesPrizeVO
 *
 * @author biu
 * @since 2020/1/7 16:56
 */
@Data
@ApiModel("抽奖活动奖品")
public class ActivitiesPrizeVO {

    @ApiModelProperty("奖品ID")
    private Integer id;

    @ApiModelProperty("奖品名称")
    private String name;

    @ApiModelProperty("奖品图片")
    private String image;

    @ApiModelProperty("奖品类型")
    private String type;

    @ApiModelProperty("奖品图片")
    private String imageOss;

    @ApiModelProperty("奖品位置")
    private Integer priority;

    public static ActivitiesPrizeVO instanceOf(TurntablePrize prize) {
        ActivitiesPrizeVO vo = new ActivitiesPrizeVO();

        vo.setId(prize.getId());
        vo.setName(prize.getName());
        vo.setImage(prize.getImage());
        vo.setType(prize.getType());
        vo.setPriority(prize.getPriority());

        return vo;
    }

    public static List<ActivitiesPrizeVO> listOf(List<TurntablePrize> prizes) {
        if (prizes == null) {
            return Collections.emptyList();
        }

        return prizes.stream().map(ActivitiesPrizeVO::instanceOf).collect(Collectors.toList());
    }
}
