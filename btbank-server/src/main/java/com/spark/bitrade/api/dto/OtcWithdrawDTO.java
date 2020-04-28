package com.spark.bitrade.api.dto;

import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.PayMode;
import com.spark.bitrade.exception.BtBankException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 一键提现DTO
 *
 * @author biu
 * @since 2019/11/28 11:33
 */
@Data
@ApiModel(value = "一键提现对象")
public class OtcWithdrawDTO {

    @ApiModelProperty(value = "支付方式, 目前仅支持BANK; ALI('支付宝'), WECHAT('微信'), BANK('银联'), EPAY('Epay')")
    private PayMode payMode;

    @ApiModelProperty(value = "数量")
    private BigDecimal amount;

    public void check() {
        if (payMode != PayMode.BANK) {
            throw new BtBankException(BtBankMsgCode.UNSUPPORTED_PAYMENT_METHOD);
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BtBankException(BtBankMsgCode.ILLEGAL_TRANSFER_AMOUNT);
        }

        if (amount.stripTrailingZeros().scale() > 8) {
            throw new BtBankException(BtBankMsgCode.ILLEGAL_TRANSFER_AMOUNT);
        }

    }
}
