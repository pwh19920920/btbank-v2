package com.spark.bitrade.api.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spark.bitrade.util.MD5Util;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * EnterpriseOrderVo
 *
 * @author biu
 * @since 2019/12/24 13:36
 */
@Data
@ApiModel(value = "订单详情")
public class EnterpriseOrderVo {

    @ApiModelProperty(value = "订单编号")
    @JSONField(name = "order_sn")
    @JsonProperty(value = "order_sn")
    private String orderSn;

    @ApiModelProperty(value = "矿工标识")
    @JSONField(name = "miner_id")
    @JsonProperty(value = "miner_id")
    private Integer minerId;

    @ApiModelProperty(value = "订单数额")
    private BigDecimal amount;

    @ApiModelProperty(value = "时间戳，单位秒")
    private Long timestamp;

    @ApiModelProperty(value = "参数校验码")
    private String sign;

    public boolean validate() {

        // 不能为空
        if (!StringUtils.hasText(orderSn) || !StringUtils.hasText(sign)) {
            return false;
        }

        // 不能为空
        if (minerId == null || timestamp == null) {
            return false;
        }

        // 不能 <= 0
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 1) {
            return false;
        }

        return true;
    }

    public boolean checkSign(String cipher) {
        String inStr = orderSn + minerId + amount + timestamp + cipher;
        return MD5Util.md5Encode(inStr).equalsIgnoreCase(sign);
    }

    /**
     * 是否过期
     *
     * @param seconds 超时秒
     * @return bool
     */
    public boolean isExpired(int seconds) {
        long now = Calendar.getInstance().getTimeInMillis() / 1000;
        return Math.abs(now - timestamp) > seconds;
    }
}
