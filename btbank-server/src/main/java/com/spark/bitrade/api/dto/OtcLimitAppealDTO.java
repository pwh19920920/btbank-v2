package com.spark.bitrade.api.dto;

import lombok.Data;

/**
 * 申诉结果DTO
 *
 * @author biu
 * @since 2019/11/28 17:47
 */
@Data
public class OtcLimitAppealDTO {

    private Long orderId;
    private Long initiatorId;
    private Long associateId;
    private Integer isSuccess;

    /**
     * 是否是获胜方
     *
     * @return bool
     */
    public boolean isWinner(Long memberId) {

        // 发起方获胜
        if (initiatorId.longValue() == memberId.longValue()) {
            return new Integer(1).equals(isSuccess);
        }

        return new Integer(0).equals(isSuccess);

    }
}
