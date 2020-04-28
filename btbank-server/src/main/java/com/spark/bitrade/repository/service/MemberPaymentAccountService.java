package com.spark.bitrade.repository.service;

import java.util.Optional;

/**
 * MemberPaymentAccountService
 *
 * @author biu
 * @since 2019/12/5 14:49
 */
public interface MemberPaymentAccountService {

    Optional<String> findAccountName(Long memberId);
}
