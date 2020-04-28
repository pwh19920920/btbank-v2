package com.spark.bitrade.constant;

/**
 * @author ww
 * @time 2019.11.10 16:42
 */
public enum MinerGradeNoteType {

    MINER_GRADE_APPLY(1, "申请金牌矿工"),
    MINER_GRADE_REFUSED(2, "申请金牌矿工失败"),
    MINER_GRADE_SUCCESS(3, "申请金牌矿工成功");
    int value;
    String name;

    MinerGradeNoteType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }


}
