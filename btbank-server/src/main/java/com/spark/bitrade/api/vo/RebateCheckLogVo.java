package com.spark.bitrade.api.vo;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * RebateCheckLogVo
 *
 * @author biu
 * @since 2019/12/9 18:06
 */
@AllArgsConstructor
@Getter
public class RebateCheckLogVo {

    private Long memberId;
    private Long walletChgId;
    private Long rebateId;
    private String msg;
    private Object response;

    public RebateCheckLogVo(String msg) {
        this.msg = msg;
    }

    public RebateCheckLogVo(String msg, Object response) {
        this.msg = msg;
        this.response = response;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
