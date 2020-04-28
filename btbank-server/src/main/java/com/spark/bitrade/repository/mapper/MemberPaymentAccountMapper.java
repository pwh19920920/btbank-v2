package com.spark.bitrade.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * MemberPaymentAccountMapper
 *
 * @author biu
 * @since 2019/12/5 14:50
 */
@Mapper
@Repository
public interface MemberPaymentAccountMapper {

    @Select("select account_name from member_payment_account where member_id = #{memberId} limit 1")
    String findAccountName(@Param("memberId") Long memberId);
}
