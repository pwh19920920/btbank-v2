package com.spark.bitrade.repository.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.BtBankFinancialBalance;
import com.spark.bitrade.repository.entity.BtBankMinerBalance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface BtBankFinancialBalanceMapper extends BaseMapper<BtBankFinancialBalance> {
    BtBankFinancialBalance findFirstByMemberId(@Param("memberId") Long memberId);

}