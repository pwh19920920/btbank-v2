package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.BusinessMinerOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商家矿池订单表(BusinessMinerOrder)表数据库访问层
 *
 * @author daring5920
 * @since 2019-11-27 17:53:27
 */
@Mapper
public interface BusinessMinerOrderMapper extends BaseMapper<BusinessMinerOrder> {

    void updateQueueStatus(@Param("ids") List<Long> ids);

    @Select("SELECT id FROM business_miner_order WHERE status=0  order by queue_status desc ,create_time asc LIMIT ${size} ")
    List<Long> findQueueOrders(@Param("size") Integer size);
}