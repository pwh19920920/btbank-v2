package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.BtBankGeneralStatement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 总报表(统计数据表)(BtBankGeneralStatement)表数据库访问层
 *
 * @author daring5920
 * @since 2019-12-16 11:19:04
 */
@Mapper
public interface BtBankGeneralStatementMapper extends BaseMapper<BtBankGeneralStatement> {

    BtBankGeneralStatement selectTotal(@Param("startTime") LocalDateTime startTime,@Param("endTime") LocalDateTime endTime);

    BtBankGeneralStatement selectNew();
}