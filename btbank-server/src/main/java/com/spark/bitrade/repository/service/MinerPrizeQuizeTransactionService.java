package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.api.vo.MinerPrizeQuizeVo;
import com.spark.bitrade.repository.entity.MinerPrizeQuizeTransaction;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 矿工参与竞猜记录(MinerPrizeQuizeTransaction)表服务接口
 *
 * @author daring5920
 * @since 2020-01-02 09:39:36
 */
public interface MinerPrizeQuizeTransactionService extends IService<MinerPrizeQuizeTransaction> {
    MinerPrizeQuizeVo queryTotal(@Param("prieQuizeId") Long prieQuizeId);
    MinerPrizeQuizeVo queryUpTotal(@Param("prieQuizeId") Long prieQuizeId);
    MinerPrizeQuizeVo queryDownTotal(@Param("prieQuizeId") Long prieQuizeId);

    /**
     * 根据用户ID获取用户所有的活动记录
     * @param memberId 用户ID
     * @return
     */
    List<MinerPrizeQuizeVo> getMinerTransaction(Long memberId, IPage<MinerPrizeQuizeTransaction> page);

    boolean collect(MinerPrizeQuizeTransaction tx, Long recordId);

    boolean release(MinerPrizeQuizeTransaction minerPrizeQuizeTransaction, Long id);
    boolean reward(MinerPrizeQuizeTransaction minerPrizeQuizeTransaction, Long recordId, BigDecimal amount);

    MinerPrizeQuizeTransaction queryMaxAmount(@Param("prieQuizeId") Long prieQuizeId);
}