package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.EnterpriseMiner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * 企业矿工表(EnterpriseMiner)表数据库访问层
 *
 * @author biu
 * @since 2019-12-23 17:15:02
 */
@Mapper
@Repository
public interface EnterpriseMinerMapper extends BaseMapper<EnterpriseMiner> {

    EnterpriseMiner findByMemberId(@Param("memberId") Long memberId);

    @Update("UPDATE `enterprise_miner` SET balance = balance + #{amount}, update_time = now() WHERE member_id = #{memberId} AND deleted = 0 AND `status` = 1 AND balance + #{amount} >= 0")
    int transfer(@Param("memberId") Long memberId, @Param("amount") BigDecimal amount);

    @Update("UPDATE `enterprise_miner` SET balance = balance - #{amount}, outlay_sum = outlay_sum + #{amount}, update_time = now() WHERE id = #{id} AND deleted = 0 AND `status` = 1 AND balance - #{amount} >= 0")
    int mining(@Param("id") Integer minerId, @Param("amount") BigDecimal amount);

    @Update("UPDATE `enterprise_miner` SET balance = balance + #{amount}, reward_sum = reward_sum + #{amount}, update_time = now() WHERE id = #{id} AND deleted = 0 AND `status` = 1 AND balance + #{amount} >= 0")
    int reward(@Param("id") Integer minerId, @Param("amount") BigDecimal amount);
}