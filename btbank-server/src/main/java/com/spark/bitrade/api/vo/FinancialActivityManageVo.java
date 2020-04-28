package com.spark.bitrade.api.vo;

import lombok.Data;

import java.util.List;

/**
 * Created by mahao on 2019/12/21.
 */
@Data
public class FinancialActivityManageVo {
    private Long id;
    private int type;
    private Long memberId;
    private List<Long> joinids;
}
