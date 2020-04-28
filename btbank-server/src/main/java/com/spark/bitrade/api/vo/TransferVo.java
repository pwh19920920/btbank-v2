package com.spark.bitrade.api.vo;

import com.spark.bitrade.exception.BtBankException;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * TransferVo
 *
 * @author biu
 * @since 2019/12/23 18:06
 */
@Data
@AllArgsConstructor
public class TransferVo {

    private static BigDecimal limit = new BigDecimal("100");

    private Long memberId;
    private Integer direction;
    private BigDecimal amount;

    public void doCheck() {
        /*if (amount == null || amount.compareTo(limit) < 0) {
            throw new BtBankException(4001, "划转数量不能少于100BT");
        }*/

        if (direction != 0 && direction != 1) {
            throw new BtBankException(4001, "无效的划转方向");
        }
    }

    public boolean isIn() {
        return direction == 1;
    }

    public boolean isOut() {
        return direction == 0;
    }
}
