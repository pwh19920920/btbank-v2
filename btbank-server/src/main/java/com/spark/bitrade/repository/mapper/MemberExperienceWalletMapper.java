package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.MemberExperienceWallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 新用户3月8之后体验金账户 Mapper 接口
 * </p>
 *
 * @author qhliao
 * @since 2020-03-06
 */
@Mapper
public interface MemberExperienceWalletMapper extends BaseMapper<MemberExperienceWallet> {

    @Select("select * from member_experience_wallet where member_id=#{memberId} and coin_id=#{coinId} limit 1")
    Optional<MemberExperienceWallet> findByMemberIdAndCoinId(@Param("memberId") Long memberId, @Param("coinId") String coinId);

    @Update("update member_experience_wallet set lock_balance=lock_balance+#{amount} where id=#{walletId}")
    int increaseLockBalance(@Param("walletId") Long walletId, @Param("amount") BigDecimal amount);

    @Update("update member_experience_wallet set lock_balance=lock_balance-#{amount} where id=#{walletId} and lock_balance-#{amount}>=0")
    int decreaseLockBalance(@Param("walletId") Long walletId, @Param("amount") BigDecimal amount);

    @Select("select id,registration_time,real_name_status from member where registration_time>=#{date}")
    List<Member> findByRegisterTime(@Param("date") Date date);

    @Select(" SELECT " +
            " m.id, " +
            " m.registration_time, " +
            " m.real_name_status, " +
            " m.inviter_id " +
            " FROM " +
            " member m LEFT JOIN member_application ma ON ma.member_id=m.id  " +
            " WHERE " +
            " m.inviter_id IS NOT NULL AND real_name_status=2 AND ma.op_type=4 AND ma.audit_status=2 AND m.registration_time>=#{date}")
    List<Member> findByRegisterTimeAndInviter(@Param("date") Date date);
}
