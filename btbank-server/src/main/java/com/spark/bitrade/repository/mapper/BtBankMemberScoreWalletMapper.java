package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.BtBankMemberScoreWallet;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
 * <p>
 * 用户挖矿积分钱包表 Mapper 接口
 * </p>
 *
 * @author qhliao
 * @since 2020-03-18
 */
public interface BtBankMemberScoreWalletMapper extends BaseMapper<BtBankMemberScoreWallet> {

    @Update("update bt_bank_member_score_wallet set balance=balance-#{score} where id=#{id} and balance-#{score}>=0")
    int decreaseScore(@Param("id") Long id, @Param("score") BigDecimal score);

}
