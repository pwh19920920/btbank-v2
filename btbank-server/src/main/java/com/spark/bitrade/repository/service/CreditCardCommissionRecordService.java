package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.CreditCardCommissionRecord;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 信用卡手续费记录 服务类
 * </p>
 *
 * @author qiliao
 * @since 2020-04-08
 */
public interface CreditCardCommissionRecordService extends IService<CreditCardCommissionRecord> {

    List<CreditCardCommissionRecord> queryUnLockList(Long memberId);

    int unLock(Long id, BigDecimal releaseAmount, int status, String remark);
}
