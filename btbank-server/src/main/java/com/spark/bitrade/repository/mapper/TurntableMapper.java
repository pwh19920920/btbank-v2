package com.spark.bitrade.repository.mapper;

import com.spark.bitrade.api.dto.ActivitiesChanceDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * TurntableMapper
 *
 * @author biu
 * @since 2020/1/8 13:41
 */
@Mapper
@Repository
public interface TurntableMapper {

    @Select("SELECT \n" +
            "  m.id,\n" +
            "  SUM(tx.`money`) reward \n" +
            "FROM\n" +
            "  `member` m \n" +
            "  LEFT JOIN `bt_bank_miner_balance_transaction` tx \n" +
            "    ON m.id = tx.`member_id` \n" +
            "WHERE  m.`inviter_id` = #{memberId} AND m.`registration_time` > #{time} AND tx.`type` IN (4, 7, 9)\n" +
            "GROUP BY m.`id`")
    List<ActivitiesChanceDTO> findMinerBalance(@Param("memberId") Long memberId, @Param("time") Date registrationTime);
}
