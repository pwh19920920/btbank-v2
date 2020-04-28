package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.api.vo.MinerRecommandListVO;
import com.spark.bitrade.repository.entity.BtBankFinancialBalance;
import com.spark.bitrade.repository.entity.BtBankMinerBalance;

import java.math.BigDecimal;

public interface BtBankFinancialBalanceService extends IService<BtBankFinancialBalance> {

    BtBankFinancialBalance findFirstByMemberId(Long memberId);

    boolean lockAmountByMemberId(Long id, BigDecimal amount);
    boolean realseAmountMemberId(Long memberId, BigDecimal amount);
}





