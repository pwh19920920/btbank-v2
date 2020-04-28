package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.api.vo.MinerPrizeQuizeVo;
import org.apache.ibatis.annotations.Mapper;
import com.spark.bitrade.repository.entity.MinerPrizeQuizeTransaction;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 矿工参与竞猜记录(MinerPrizeQuizeTransaction)表数据库访问层
 *
 * @author daring5920
 * @since 2020-01-02 09:39:36
 */
@Mapper
public interface MinerPrizeQuizeTransactionMapper extends BaseMapper<MinerPrizeQuizeTransaction> {
    MinerPrizeQuizeVo queryTotal(@Param("prieQuizeId") Long prieQuizeId);
    MinerPrizeQuizeVo queryUpTotal(@Param("prieQuizeId") Long prieQuizeId);
    MinerPrizeQuizeVo queryDownTotal(@Param("prieQuizeId") Long prieQuizeId);
    MinerPrizeQuizeTransaction queryMaxAmount(@Param("prieQuizeId") Long prieQuizeId);
    List<MinerPrizeQuizeVo> getMinerTransaction(@Param("memberId") Long memberId, IPage<MinerPrizeQuizeTransaction> page);
}