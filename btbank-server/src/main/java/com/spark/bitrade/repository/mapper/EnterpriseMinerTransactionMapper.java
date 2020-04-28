package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.EnterpriseMinerTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 企业矿工流水表(EnterpriseMinerTransaction)表数据库访问层
 *
 * @author biu
 * @since 2019-12-23 17:14:35
 */
@Mapper
@Repository
public interface EnterpriseMinerTransactionMapper extends BaseMapper<EnterpriseMinerTransaction> {

    BigDecimal sumReward(@Param("memberId") Long memberId, @Param("start") Date start, @Param("end") Date end);
}