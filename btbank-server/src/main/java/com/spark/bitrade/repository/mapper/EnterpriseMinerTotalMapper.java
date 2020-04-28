package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.EnterpriseMinerTotal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 企业挖矿汇总表(EnterpriseMinerTotal)表数据库访问层
 *
 * @author zyj
 * @since 2019-12-27 11:33:00
 */
@Mapper
public interface EnterpriseMinerTotalMapper extends BaseMapper<EnterpriseMinerTotal> {

    /**
     * 转入次数、人数、总额
     *
     * @param startTime
     * @return
     */
    EnterpriseMinerTotal getInto(@Param("startTime") String startTime);

    /**
     * 转出、挖矿、佣金
     *
     * @param startTime
     * @return
     */
    EnterpriseMinerTotal getSendAndMineAndReward(@Param("startTime") String startTime);


}