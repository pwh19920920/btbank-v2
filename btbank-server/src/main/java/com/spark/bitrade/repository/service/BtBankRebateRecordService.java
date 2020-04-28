package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.api.dto.RebateRecordDTO;
import com.spark.bitrade.api.vo.BtBankRebateRecordVO;
import com.spark.bitrade.api.vo.MyRewardListVO;
import com.spark.bitrade.repository.entity.BtBankRebateRecord;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

public interface BtBankRebateRecordService extends IService<BtBankRebateRecord> {


    IPage<BtBankRebateRecordVO> getRebateRecordAndNamePage(Page<BtBankRebateRecordVO> page, @RequestParam("memberId") Long memberId);

    /**
     * 我的奖励列表
     *
     * @param cuurent
     * @param size
     * @param memberId
     * @return
     */
    MyRewardListVO getMyRewards(Long cuurent, Long size, @RequestParam("memberId") Long memberId);

    List<RebateRecordDTO> getRecordsCreatedAfter(Date date);
}








