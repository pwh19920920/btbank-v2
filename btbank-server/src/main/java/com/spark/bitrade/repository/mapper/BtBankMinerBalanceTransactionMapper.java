package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.BtBankMinerBalanceTransaction;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Mapper
public interface BtBankMinerBalanceTransactionMapper extends BaseMapper<BtBankMinerBalanceTransaction> {
    @Update("update bt_bank_miner_balance_transaction set balance=balance-#{payDecimal} where id=#{id} and balance >=#{payDecimal}")
    int spendBalanceWithIdAndBalance(@Param("id") Long id, @Param("payDecimal") BigDecimal payDecimal);

    @ResultMap("")
    BigDecimal getYestodayMinerBalanceTransactionsSumByMemberId(@Param("memberId") Long memberId, @Param("types") List types);

    List<BtBankMinerBalanceTransaction> listNeedRebate();

    boolean markRebateProcessedById(Long id);

    List<BtBankMinerBalanceTransaction> countProfitByType(@Param("limitTime") Date limitTime);

    @Select("select sum(money) from bt_bank_miner_balance_transaction where member_id=#{memberId} and create_time>#{date} and type=1 group by member_id")
    BigDecimal sum38AfterTransfer(@Param("memberId") Long memberId, @Param("date") Date date);
    /**
     * 查询有效矿工
     *
     * @return
     */
    @Select("SELECT tx.member_id FROM bt_bank_miner_balance_transaction tx where type = 1 GROUP BY tx.member_id")
    Long[] getValidMiners();
}