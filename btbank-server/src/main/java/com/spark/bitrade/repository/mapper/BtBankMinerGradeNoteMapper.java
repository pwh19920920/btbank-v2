package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.BtBankMinerGradeNote;
import org.apache.ibatis.annotations.Mapper;import org.apache.ibatis.annotations.Param;

@Mapper
public interface BtBankMinerGradeNoteMapper extends BaseMapper<BtBankMinerGradeNote> {
    /**
     * 查询指定用户的最后一条申请记录
     *
     * @param memberId
     * @return
     */
    BtBankMinerGradeNote findLastRecordByMemberId(@Param("memberId") Long memberId);
}