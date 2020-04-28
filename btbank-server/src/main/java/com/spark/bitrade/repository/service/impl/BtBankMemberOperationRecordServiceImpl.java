package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.mapper.BtBankMemberOperationRecordMapper;
import com.spark.bitrade.repository.entity.BtBankMemberOperationRecord;
import com.spark.bitrade.repository.service.BtBankMemberOperationRecordService;
import org.springframework.stereotype.Service;

/**
 * 会员操作记录表(BtBankMemberOperationRecord)表服务实现类
 *
 * @author daring5920
 * @since 2019-11-27 17:53:26
 */
@Service("btBankMemberOperationRecordService")
public class BtBankMemberOperationRecordServiceImpl extends ServiceImpl<BtBankMemberOperationRecordMapper, BtBankMemberOperationRecord> implements BtBankMemberOperationRecordService {

}