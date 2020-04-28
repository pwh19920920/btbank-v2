package com.spark.bitrade.constant;

/**
 * EnterpriseTransactionType
 *
 * @author biu
 * @since 2019/12/24 10:01
 */
public enum EnterpriseTransactionType {
    // 1：转入 2：转出 3：抢单挖矿 4：挖矿收益
    None, TransferIn, TransferOut, MiningOrder, MiningReward;

    public static EnterpriseTransactionType of(Integer ord) {
        if (ord == null) return None;
        for (EnterpriseTransactionType value : values()) {
            if (ord == value.ordinal()) {
                return value;
            }
        }
        return None;
    }

    public static boolean isTransfer(Integer ord) {
        if (ord == null) return false;
        return TransferOut.ordinal() == ord || TransferIn.ordinal() == ord;
    }

    public int code() {
        return ordinal();
    }
}
