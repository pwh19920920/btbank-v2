package com.spark.bitrade.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ww
 * @time 2019.11.12 09:24
 */

@Data
@ApiModel("推荐人列表信息")
public class MinerRecommandListVO extends RespObjectList<MinerRecommandVO> {
    //    @ApiModelProperty("推荐的人数")
//    Long recommandNum = 0L;
    @ApiModelProperty("推荐成功挖矿的人数")
    Integer recommandSuccessNum = 0;

//    @ApiModelProperty("推荐列表")
//    List<MinerRecommandVO> list = new ArrayList();
}
