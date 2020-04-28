package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.BtBankMinerPerRank;

import java.math.BigDecimal;

/**
 * 有效矿工业绩排名统计(BtBankMinerPerRank)表服务接口
 *
 * @author daring5920
 * @since 2020-03-18 15:58:04
 */
public interface BtBankMinerPerRankService extends IService<BtBankMinerPerRank> {
    /**
     * 统计有效直推
     *
     * @param minerId
     * @return
     */
    BtBankMinerPerRank getSub(Long minerId);

    /**
     * 某人业绩统计
     *
     * @param memberId
     * @return
     */
    BigDecimal getPer(Long memberId);


}