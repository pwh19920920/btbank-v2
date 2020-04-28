package com.spark.bitrade.constant;

import lombok.Getter;

@Getter
public enum ApplyGoldMinerCode {

    /**
     * 已经是金牌矿工
     */
    ALREADY_GOLD_MINER(0, "已经是金牌矿工"),
    /**
     * 不符合申请条件
     */
    Ineligible(1, "不符合申请条件"),
    /**
     * 申请中
     */
    PENDING(2, "申请中"),
    /**
     * 审核未通过
     */
    APPLY_FAILED(3, "审核未通过"),
    /**
     * 符合申请条件
     */
    CONFORM(4, "符合申请条件"),
    /**
     * 没有申请记录
     */
    NO_RECORD(5, "没有申请记录"),

    SUCCESS(6, "申请成功");

    ApplyGoldMinerCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private int code;
    private String msg;
}
