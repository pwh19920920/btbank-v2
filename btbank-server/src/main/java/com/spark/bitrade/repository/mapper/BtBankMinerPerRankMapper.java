package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.BtBankMinerPerRank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 有效矿工业绩排名统计(BtBankMinerPerRank)表数据库访问层
 *
 * @author daring5920
 * @since 2020-03-18 15:58:04
 */
@Mapper
public interface BtBankMinerPerRankMapper extends BaseMapper<BtBankMinerPerRank> {

    /**
     * 统计有效直推
     *
     * @param minerId
     * @return
     */
    BtBankMinerPerRank getSub(@Param("minerId") Long minerId);

    /**
     * 查询直推的用户id
     *
     * @param memberId
     * @return
     */
    List<Long> getSubId(@Param("memberId") Long memberId);

    /**
     * 统计业绩
     *
     * @return
     */
    BigDecimal getOnesPer(@Param("members") List<Long> members);

}