package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.api.vo.MinerRecommandVO;
import com.spark.bitrade.repository.entity.BtBankMinerBalance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface BtBankMinerBalanceMapper extends BaseMapper<BtBankMinerBalance> {
    BtBankMinerBalance findFirstByMemberId(@Param("memberId") Long memberId);

    int updateIncBalanceAmount(@Param("memberId") Long memberId, @Param("addedBalanceAmount") BigDecimal addedBalanceAmount);

    /**
     * 自动派单匹配矿工
     *
     * @param amount
     * @return true
     * @author shenzucai
     * @time 2019.10.24 20:16
     */
    BtBankMinerBalance dispatchMiner(@Param("amount") BigDecimal amount);

    /**
     * 抢单扣款
     *
     * @param money
     * @param reward
     * @return
     */

    @Update("update bt_bank_miner_balance set balance_amount = balance_amount-#{money},lock_amount=lock_amount+#{money},processing_reward_sum = processing_reward_sum+#{reward},update_time=now() where id=#{minerBalanceId} and  balance_amount>#{money}")
    int grabSuccAndUpdate(@Param("minerBalanceId") Long minerBalanceId, @Param("money") BigDecimal money, @Param("reward") BigDecimal reward);

    /**
     * 获取用户推荐成功数量
     *
     * @param memberId
     * @return
     */
    @Select("select count(m.id) from member m " +
            "left join bt_bank_miner_balance b on b.member_id = m.id " +
            "where m.inviter_id = #{memberId} " +
            "and b.miner_grade >= 1")
    int getRecommandAndChargeSuccMemberCount(@Param("memberId") Long memberId);


    //@Select("SELECT m.id, m.mobile_phone AS mobile_phone, m.username AS username, m.registration_time AS registration_time, CASE WHEN info.miner_grade = 2 THEN 2 ELSE 1 END AS miner_grade, CASE WHEN tran.type = 1 THEN 1 ELSE 0 END AS is_miner FROM member AS m LEFT JOIN bt_bank_miner_balance AS info ON m.id = info.member_id LEFT JOIN bt_bank_miner_balance_transaction AS tran ON m.id = tran.member_id WHERE m.inviter_id = #{memberId} AND ( tran.type = 1 OR ISNULL(tran.type) ) GROUP BY m.id")
    IPage<MinerRecommandVO> getRecommandList(Page<MinerRecommandVO> page, @Param("memberId") Long memberId);
}