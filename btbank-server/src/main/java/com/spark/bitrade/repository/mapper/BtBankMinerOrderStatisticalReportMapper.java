package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.BtBankMinerOrderStatisticalReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface BtBankMinerOrderStatisticalReportMapper extends BaseMapper<BtBankMinerOrderStatisticalReport> {

    BtBankMinerOrderStatisticalReport getOrderReportFromBalanceTransaction(@Param("date") Date date);

}