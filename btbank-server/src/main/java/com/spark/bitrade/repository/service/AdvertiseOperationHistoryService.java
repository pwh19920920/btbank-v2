package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.api.vo.AdvertiseHistoryVo;
import com.spark.bitrade.repository.entity.AdvertiseOperationHistory;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 广告操作记录表(AdvertiseOperationHistory)表服务接口
 *
 * @author daring5920
 * @since 2019-11-27 17:53:22
 */
public interface AdvertiseOperationHistoryService extends IService<AdvertiseOperationHistory> {

    List<Long> getMemberAdHistoryIds(Long memberId);

    List<AdvertiseOperationHistory> getAdHistroys(Long adId);

    List<AdvertiseOperationHistory> listAdHistroysByMemberId(Long memberId, Date begin,Date end);
    /**
     * 检查商家广告休业时间
     *
     * @param memberId
     * @return
     */

    Boolean checkBusinessAdClosing(Long memberId);
}