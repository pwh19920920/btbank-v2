package com.spark.bitrade.repository.mapper;

import com.spark.bitrade.api.dto.WelfareRewardStateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * WelfareMapper
 *
 * @author biu
 * @since 2020/4/9 16:41
 */
@Mapper
@Repository
public interface WelfareMapper {

    @Select("SELECT count(1) FROM member_wallet mw WHERE mw.member_id=#{memberId} AND mw.coin_id='BT' AND mw.balance >= #{amount}")
    int checkWalletBalance(@Param("memberId") Long memberId, @Param("amount") BigDecimal amount);

    @Select("select count(1) from bt_bank_miner_balance_transaction tx WHERE tx.type = 1 and tx.member_id = #{memberId}")
    int isAvailableMiner(@Param("memberId") Long memberId);

    @Select("SELECT count(1) from member_application WHERE member_id = #{memberId} AND op_type=4 AND audit_status=2")
    int isAutoAuthRealName(@Param("memberId") Long memberId);

    @Select("select IFNULL(MAX(period)+1,1) v from welfare_activity where type = #{type}")
    Integer getMaxPeriod(@Param("type") Integer type);

    @Select("select p.`status`, p.receive_time,i.id from bt_bank_member_pending_ward p left join welfare_involvement i on p.tx_id = i.id where p.type in (2,3) and i.recommend_status = 1")
    List<WelfareRewardStateDto> findUnReceivedRewardRecords();
}
