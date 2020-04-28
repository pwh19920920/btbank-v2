package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.BtBankMinerGradeNote;

public interface BtBankMinerGradeNoteService extends IService<BtBankMinerGradeNote> {

    BtBankMinerGradeNote findLastRecordByMemberId(Long memberId);

}





