package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.BtBankMemberOperationRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员操作记录表(BtBankMemberOperationRecord)表数据库访问层
 *
 * @author daring5920
 * @since 2019-11-27 17:53:25
 */
@Mapper
public interface BtBankMemberOperationRecordMapper extends BaseMapper<BtBankMemberOperationRecord> {

}