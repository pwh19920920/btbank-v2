package com.spark.bitrade.repository.service.impl;

import com.spark.bitrade.repository.mapper.MemberPaymentAccountMapper;
import com.spark.bitrade.repository.service.MemberPaymentAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * MemberPaymentAccountServiceImpl
 *
 * @author biu
 * @since 2019/12/5 14:49
 */
@Service
public class MemberPaymentAccountServiceImpl implements MemberPaymentAccountService {

    private MemberPaymentAccountMapper mapper;

    @Autowired
    public void setMapper(MemberPaymentAccountMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<String> findAccountName(Long memberId) {
        String accountName = mapper.findAccountName(memberId);
        if (StringUtils.hasText(accountName)) {
            return Optional.of(accountName);
        }
        return Optional.empty();
    }
}
