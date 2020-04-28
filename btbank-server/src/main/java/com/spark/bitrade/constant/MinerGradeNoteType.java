package com.spark.bitrade.constant;

import lombok.Getter;

@Getter
public enum MinerGradeNoteType {

    /**
     * 未处理
     */
    PENDING(0, "未处理"),
    /**
     * 审核未通过
     */
    FAILD(1, "审核未通过"),
    /**
     * 审核通过
     */
    PASS(2, "审核通过"),
    /**
     * 管理员手动设置
     */
    MGR_SET(3, "管理员手动设置"),
    /**
     * 管理员手动取消
     */
    MGR_UNSET(4, "管理员手动取消");

    MinerGradeNoteType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private int code;
    private String msg;
}
