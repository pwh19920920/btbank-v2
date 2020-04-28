package com.spark.bitrade.repository.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.BtBankFinancialBalance;
import com.spark.bitrade.repository.entity.MinerPrizeQuizeTransaction;
import com.spark.bitrade.repository.mapper.BtBankFinancialBalanceMapper;
import com.spark.bitrade.repository.service.BtBankFinancialBalanceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;


@Service
public class BtBankFinancialBalanceServiceImpl extends ServiceImpl<BtBankFinancialBalanceMapper, BtBankFinancialBalance> implements BtBankFinancialBalanceService {


    @Override
    public BtBankFinancialBalance findFirstByMemberId(Long memberId) {
        return this.baseMapper.findFirstByMemberId(memberId);
    }

    @Override
    public boolean lockAmountByMemberId(Long memberId, BigDecimal amount) {

        return this.lambdaUpdate()
                .setSql("balance_amount =  balance_amount + " + amount.abs() )
                .eq(BtBankFinancialBalance::getMemberId, memberId).ge(BtBankFinancialBalance::getBalanceAmount,BigDecimal.ZERO).update();
    }
    @Override
    public boolean realseAmountMemberId(Long memberId, BigDecimal amount) {
        return this.lambdaUpdate()
                .setSql("balance_amount =  balance_amount -  " + amount.abs() )
                .eq(BtBankFinancialBalance::getMemberId, memberId).ge(BtBankFinancialBalance::getBalanceAmount,amount.abs()).update();
    }

}





