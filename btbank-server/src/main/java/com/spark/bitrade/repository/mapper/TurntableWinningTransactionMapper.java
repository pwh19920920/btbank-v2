package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.TurntableWinningTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 自动发放记录表(TurntableWinningTransaction)表数据库访问层
 *
 * @author biu
 * @since 2020-01-09 10:05:01
 */
@Mapper
@Repository
public interface TurntableWinningTransactionMapper extends BaseMapper<TurntableWinningTransaction> {

}