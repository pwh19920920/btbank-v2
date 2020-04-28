package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.api.dto.RebateRecordDTO;
import com.spark.bitrade.api.vo.BtBankRebateRecordVO;
import com.spark.bitrade.api.vo.MyRewardVO;
import com.spark.bitrade.repository.entity.BtBankRebateRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface BtBankRebateRecordMapper extends BaseMapper<BtBankRebateRecord> {


    IPage<BtBankRebateRecordVO> getRebateRecordAndNamePage(Page<BtBankRebateRecordVO> page, @Param("memberId") Long memberId);

    List<MyRewardVO> getMyReward1(@Param("memberId") Long memberId);

    List<MyRewardVO> getMyReward2(@Param("memberId") Long memberId);

    List<MyRewardVO> getMyReward3(@Param("memberId") Long memberId);

    List<MyRewardVO> getMyReward4(@Param("memberId") Long memberId);

    List<MyRewardVO> getMyReward5(@Param("memberId") Long memberId);

    List<MyRewardVO> getMyReward6(@Param("memberId") Long memberId);

    List<MyRewardVO> getMyReward7(@Param("memberId") Long memberId);

    List<MyRewardVO> getMyReward8(@Param("memberId") Long memberId);

    List<MyRewardVO> getMyReward9(@Param("memberId") Long memberId);

    List<MyRewardVO> getMyReward10(@Param("memberId") Long memberId);

    List<MyRewardVO> getMyReward11(@Param("memberId") Long memberId);

    List<RebateRecordDTO> getRecordsCreatedAfter(@Param("date") Date date);
}