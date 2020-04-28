package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.MemberTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * (MemberTransaction)表数据库访问层
 *
 * @author yangch
 * @since 2019-06-15 16:27:30
 */
@Mapper
public interface MemberTransactionMapper extends BaseMapper<MemberTransaction> {

    /**
     * 收益统计
     * @param memberId
     * @return
     */
    Double selectProfitCount(@Param("memberId") Long memberId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 收益详情列表
     * @param memberId
     * @return
     */
    List<MemberTransaction> profitList(IPage<MemberTransaction> page, @Param("memberId") Long memberId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}