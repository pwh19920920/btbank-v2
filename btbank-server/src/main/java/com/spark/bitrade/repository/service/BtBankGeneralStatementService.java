package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.BtBankGeneralStatement;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 总报表(统计数据表)(BtBankGeneralStatement)表服务接口
 *
 * @author daring5920
 * @since 2019-12-16 11:19:04
 */
public interface BtBankGeneralStatementService extends IService<BtBankGeneralStatement> {

    /**
     * 总报表统计查询
     * @return
     */
    Boolean selectTotal(LocalDateTime startTime,LocalDate time);

    /**
     * 查询最新一天报表数据
     * @return
     */
    BtBankGeneralStatement selectNew();
}