package com.spark.bitrade.constant;

public enum MinerScoreTransactionType {



    GRAB_COMMISSION_TRANSFER_OUT(4, "抢单佣金转出"),

    DISPATCH_COMMISSION_TRANSFER_OUT(7, "派单佣金转出"),

    FIEXD_COMMISSION_TRANSFER_OUT(9, "固定佣金转出"),

    FINANCIAL_ACTIVITY_PROFIT(56,"大宗挖矿利息"),
    ;


    int value;
    String name;


    MinerScoreTransactionType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

}
