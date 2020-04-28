package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.BtBankMinerOrderStatisticalReport;

import java.util.Date;

public interface BtBankMinerOrderStatisticalReportService extends IService<BtBankMinerOrderStatisticalReport> {

    BtBankMinerOrderStatisticalReport getOrderReportFromBalanceTransaction(Date date);

    Boolean saveTransaction(BtBankMinerOrderStatisticalReport fetchReport);
}
