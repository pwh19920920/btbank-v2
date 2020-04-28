package com.spark.bitrade.api.vo;

import com.spark.bitrade.repository.entity.AdvertiseOperationHistory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
@Data
@ApiModel("广告历史记录")
public class AdvertiseHistoryVo {
    @ApiModelProperty("时间")
    private Date createTime;

    @ApiModelProperty("操作状态")
    private String advertiseStatus;

    /**
     * 累计广告时长
     */
    private String cumulativeTime;

    public static AdvertiseHistoryVo of(AdvertiseOperationHistory advertiseOperationHistory) {
        AdvertiseHistoryVo  ddvertiseHistoryVo = new  AdvertiseHistoryVo();
        ddvertiseHistoryVo.setCreateTime(advertiseOperationHistory.getCreateTime());
        if(advertiseOperationHistory.getNewStatus().equals(0)){
            ddvertiseHistoryVo.setAdvertiseStatus("上架");
        } else if(advertiseOperationHistory.getNewStatus().equals(1)){
            ddvertiseHistoryVo.setAdvertiseStatus("下架");
        }else{
            ddvertiseHistoryVo.setAdvertiseStatus("其他");
        }
        return ddvertiseHistoryVo;
    }
}
