package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.spark.bitrade.repository.entity.PrizeQuizeRecord;

/**
 * 往期竞猜记录(PrizeQuizeRecord)表数据库访问层
 *
 * @author daring5920
 * @since 2020-01-02 09:58:28
 */
@Mapper
public interface PrizeQuizeRecordMapper extends BaseMapper<PrizeQuizeRecord> {

}