package com.spark.bitrade.api.vo;

import com.spark.bitrade.repository.entity.WelfareActivity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Calendar;
import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "福利包活动")
public class WelfareActivityVo extends WelfareActivity {

    @ApiModelProperty(value = "状态 0:未开始 1：进行中 2: 已封盘 3：已结束")
    private Integer status;

    public static WelfareActivityVo of(WelfareActivity activity) {
        if (activity == null) {
            return null;
        }
        WelfareActivityVo vo = new WelfareActivityVo();
        vo.setId(activity.getId());
        vo.setName(activity.getName());
        vo.setType(activity.getType());
        vo.setPeriod(activity.getPeriod());
        vo.setLockTime(activity.getLockTime());
        vo.setEarningRate(activity.getEarningRate());
        vo.setOpenningTime(activity.getOpenningTime());
        vo.setClosingTime(activity.getClosingTime());
        vo.setReleaseTime(activity.getReleaseTime());
        vo.setAmount(activity.getAmount());
        vo.setRemark(activity.getRemark());
        vo.setCreateTime(activity.getCreateTime());
        vo.setUpdateTime(activity.getUpdateTime());

        Date now = Calendar.getInstance().getTime();
        int state = 0;
        if (now.compareTo(activity.getOpenningTime()) < 0) {
            state = 0;
        }
        if (now.compareTo(activity.getOpenningTime()) > -1 && now.compareTo(activity.getClosingTime()) < 0) {
            state = 1;
        }

        if (now.compareTo(activity.getClosingTime()) > -1 && now.compareTo(activity.getReleaseTime()) < 0) {
            state = 2;
        }
        if (now.compareTo(activity.getReleaseTime()) > 0) {
            state = 3;
        }
        vo.setStatus(state);
        return vo;
    }
}
