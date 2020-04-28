package com.spark.bitrade.api.vo;

import com.spark.bitrade.repository.entity.EnterpriseMinerApplication;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ApplicationResultVO
 *
 * @author biu
 * @since 2019/12/24 16:23
 */
@Data
@ApiModel("企业矿工申请结果")
public class ApplicationResultVO {

    @ApiModelProperty(value = "申请类型 1：加入申请 2：退出申请")
    private int type;

    @ApiModelProperty(value = "申请状态 0: 审核中 1：已通过 2: 已拒绝")
    private int status;

    @ApiModelProperty(value = "原因说明")
    private String remark;

    @ApiModelProperty(value = "审核通过后的结果")
    private EnterpriseMinerVO result;

    public static ApplicationResultVO of(EnterpriseMinerApplication app) {
        ApplicationResultVO vo = new ApplicationResultVO();

        vo.setType(app.getType());
        vo.setStatus(app.getStatus());
        vo.setRemark(app.getRemark());

        return vo;
    }
}
