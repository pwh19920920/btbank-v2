package com.spark.bitrade.biz;

import com.spark.bitrade.api.vo.CreditCardCommissionRefundVO;

import java.math.BigDecimal;

public interface CreditCardCommissionService {
    void refund(CreditCardCommissionRefundVO vo);

    void unLockRefund(BigDecimal amount, Long memberId);
}
