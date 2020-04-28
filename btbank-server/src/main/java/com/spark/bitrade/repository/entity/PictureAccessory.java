package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 图片附件表(PictureAccessory)表实体类
 *
 * @author daring5920
 * @since 2019-12-01 15:36:37
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "图片附件表")
public class PictureAccessory {

    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 类型{0:付款回执}
     */
    @ApiModelProperty(value = "类型{0:付款回执,1:转账信息}", example = "")
    private Integer type;

    /**
     * 关联id
     */
    @ApiModelProperty(value = "关联id", example = "")
    private String refId;

    /**
     * 附件地址
     */
    @ApiModelProperty(value = "附件地址", example = "")
    private String urlPath;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "")
    private String remark;


}