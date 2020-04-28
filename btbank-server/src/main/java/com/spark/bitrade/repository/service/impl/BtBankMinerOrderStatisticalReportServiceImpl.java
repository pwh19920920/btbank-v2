package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.BtBankMinerOrderStatisticalReport;
import com.spark.bitrade.repository.mapper.BtBankMinerOrderStatisticalReportMapper;
import com.spark.bitrade.repository.service.BtBankMinerOrderStatisticalReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class BtBankMinerOrderStatisticalReportServiceImpl extends ServiceImpl<BtBankMinerOrderStatisticalReportMapper, BtBankMinerOrderStatisticalReport> implements BtBankMinerOrderStatisticalReportService {


    @Override
    public BtBankMinerOrderStatisticalReport getOrderReportFromBalanceTransaction(Date date) {
        return this.baseMapper.getOrderReportFromBalanceTransaction(date);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveTransaction(BtBankMinerOrderStatisticalReport fetchReport) {
        return baseMapper.insert(fetchReport) > 0 ? Boolean.TRUE : Boolean.FALSE;
    }
}
