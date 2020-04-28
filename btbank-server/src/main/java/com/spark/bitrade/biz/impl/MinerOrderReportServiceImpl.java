package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.biz.MinerOrderReportService;
import com.spark.bitrade.repository.entity.BtBankMinerBalanceTransaction;
import com.spark.bitrade.repository.entity.BtBankMinerOrderStatisticalReport;
import com.spark.bitrade.repository.service.BtBankMinerBalanceTransactionService;
import com.spark.bitrade.repository.service.BtBankMinerOrderStatisticalReportService;
import com.spark.bitrade.util.IdWorkByTwitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * @author ww
 * @time 2019.11.19 17:42
 */
@Slf4j
@Service
public class MinerOrderReportServiceImpl implements MinerOrderReportService {


    @Autowired
    private IdWorkByTwitter idWorkByTwitter;
    @Autowired
    BtBankMinerOrderStatisticalReportService orderStatisticalReportService;

    @Autowired
    BtBankMinerBalanceTransactionService balanceTransactionService;


    @Override
    public void orderStatisticalReport(Date date) {


        BtBankMinerBalanceTransaction transaction = balanceTransactionService.getOne(
                new QueryWrapper<BtBankMinerBalanceTransaction>().lambda().orderBy(true, true, BtBankMinerBalanceTransaction::getCreateTime).last("limit 1")
        );

        log.info("-----BtBankMinerOrderStatisticalReport-----transaction------------ {}",transaction);

        BtBankMinerOrderStatisticalReport report = orderStatisticalReportService.getOne(
                new QueryWrapper<BtBankMinerOrderStatisticalReport>().lambda().orderByDesc(BtBankMinerOrderStatisticalReport::getReportDate).last("limit 1")
        );
        log.info("----BtBankMinerOrderStatisticalReport------report------------ {}",report);

        //有记录才进行统计
        if (transaction != null) {


            Calendar reportCal = Calendar.getInstance();
            reportCal.setTime(report.getReportDate());

            reportCal.set(Calendar.HOUR_OF_DAY, 0);
            reportCal.set(Calendar.MINUTE, 0);
            reportCal.set(Calendar.SECOND, 0);
            reportCal.set(Calendar.MILLISECOND, 0);


            Calendar endCal = Calendar.getInstance();
            endCal.setTime(new Date());


            while (reportCal.getTime().getTime()/(1000*60*60*24) < endCal.getTime().getTime()/(1000*60*60*24)) {

                BtBankMinerOrderStatisticalReport todayReport = orderStatisticalReportService.getOne(
                        new QueryWrapper<BtBankMinerOrderStatisticalReport>().lambda().eq(BtBankMinerOrderStatisticalReport::getReportDate, reportCal.getTime())
                );

                if (todayReport == null) {
                    BtBankMinerOrderStatisticalReport fetchReport = orderStatisticalReportService.getOrderReportFromBalanceTransaction(reportCal.getTime());
                    fetchReport.setId(idWorkByTwitter.nextId());
                    fetchReport.setReportDate(reportCal.getTime());
                    fetchReport.setCreateTime(new Date());
                    fetchReport.setUpdateTime(new Date());
                    Boolean saveSuccess = orderStatisticalReportService.saveTransaction(fetchReport);
                    if(!saveSuccess){
                        log.error("-------BtBankMinerOrderStatisticalReport---保存失败 日期： {} 数据 ：{}----------",reportCal.getTime(),fetchReport);
                        break;
                    }
                    //加入统计数量
                }

                reportCal.add(Calendar.DATE, 1);
            }
        }


    }
}
