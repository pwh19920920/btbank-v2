package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.CreditCardCommissionRecord;
import com.spark.bitrade.repository.mapper.CreditCardCommissionRecordMapper;
import com.spark.bitrade.repository.service.CreditCardCommissionRecordService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 信用卡手续费记录 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2020-04-08
 */
@Service
public class CreditCardCommissionRecordServiceImpl extends ServiceImpl<CreditCardCommissionRecordMapper, CreditCardCommissionRecord> implements CreditCardCommissionRecordService {

    @Override
    public List<CreditCardCommissionRecord> queryUnLockList(Long memberId) {
        return baseMapper.queryUnLockList(memberId);
    }

    @Override
    public int unLock(Long id, BigDecimal releaseAmount, int status, String remark) {
        return baseMapper.unLock(id, releaseAmount, status, remark);
    }
}
