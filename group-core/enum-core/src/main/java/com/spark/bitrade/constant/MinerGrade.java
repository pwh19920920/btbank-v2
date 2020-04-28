package com.spark.bitrade.constant;

/**
 * @author ww
 * @time 2019.11.10 14:48
 */

public enum MinerGrade {

    /**
     * 普通用户
     */
    NONE(0, "普通用户"),
    /**
     * 银牌矿工
     */
    SILVER_MINER(1, "银牌矿工"),
    /**
     * 金牌矿工
     */
    GOLD_MINER(2, "金牌矿工"),
    /**
     * 银牌创世矿工
     */
    SILVER_CREATION_MINER(3,"银牌创世矿工"),
    ;

    int gradeId;
    String name;

    MinerGrade(int gradeId, String name) {
        this.gradeId = gradeId;
        this.name = name;
    }

    public int getGradeId() {
        return gradeId;
    }

    public String getName() {
        return name;
    }


}
