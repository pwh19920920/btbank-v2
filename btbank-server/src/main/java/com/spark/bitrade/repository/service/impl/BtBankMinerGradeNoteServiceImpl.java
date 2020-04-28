package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.BtBankMinerGradeNote;
import com.spark.bitrade.repository.mapper.BtBankMinerGradeNoteMapper;
import com.spark.bitrade.repository.service.BtBankMinerGradeNoteService;
import org.springframework.stereotype.Service;

/**
 * (BtBankMinerGradeNote)表服务实现类
 *
 * @author daring5920
 * @since 2019-11-10 16:34:15
 */
@Service
public class BtBankMinerGradeNoteServiceImpl extends ServiceImpl<BtBankMinerGradeNoteMapper, BtBankMinerGradeNote> implements BtBankMinerGradeNoteService {
    @Override
    public BtBankMinerGradeNote findLastRecordByMemberId(Long memberId) {
        return baseMapper.findLastRecordByMemberId(memberId);
    }
}







