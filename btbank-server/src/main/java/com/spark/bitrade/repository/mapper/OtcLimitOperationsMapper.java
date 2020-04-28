package com.spark.bitrade.repository.mapper;

import com.spark.bitrade.api.dto.OtcLimitAppealDTO;
import com.spark.bitrade.api.dto.OtcLimitDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * OtcLimitOperationsMapper
 *
 * @author biu
 * @since 2019/11/28 17:14
 */
@Mapper
@Repository
public interface OtcLimitOperationsMapper {

    List<OtcLimitDTO> findOtcLimitOrder(@Param("memberId") Long memberId, @Param("date") Date date);

    List<OtcLimitAppealDTO> findLimitAppealByOrderIds(@Param("ids") List<Long> ids);

    @Select(" SELECT COUNT(1) FROM `member_wallet` w WHERE w.`coin_id` = 'BT' AND w.`member_id` = #{memberId} AND w.`balance` >= #{minimum}")
    int balanceIsEnough(@Param("memberId") Long memberId, @Param("minimum") BigDecimal minimum);

    @Select("SELECT COUNT(1) FROM `finc_member_account` fm WHERE fm.`member_type` = 1 AND fm.`member_id` = #{memberId}")
    int countInFincMemberAccount(@Param("memberId") Long memberId);

    @Select("SELECT COUNT(1) FROM `member_permissions_relieve_task` t WHERE t.`member_id` = #{memberId} AND t.`relieve_permissions_type` = #{type}  AND t.`status` = 0")
    int countInPunishment(@Param("memberId") Long memberId, @Param("type") Integer type);
}
