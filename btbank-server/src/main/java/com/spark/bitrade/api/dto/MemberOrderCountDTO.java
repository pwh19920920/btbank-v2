package com.spark.bitrade.api.dto;

import lombok.Data;

/**
 * MemberOrderCountDTO
 *
 * @author biu
 * @since 2019/12/11 17:39
 */
@Data
public class MemberOrderCountDTO {

    private Long id;
    private String realName;
    private Integer orders;

    public boolean isAvailable(Long memberId) {
        if (id == null || id.equals(memberId)) {
            return false;
        }

        return orders == null || orders == 0;
    }
}
