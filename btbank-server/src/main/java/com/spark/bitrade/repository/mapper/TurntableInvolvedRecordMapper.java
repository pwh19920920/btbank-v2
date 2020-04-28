package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.TurntableInvolvedRecord;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 参与记录详情表(TurntableInvolvedRecord)表数据库访问层
 *
 * @author biu
 * @since 2020-01-08 17:25:48
 */
@Mapper
@Repository
public interface TurntableInvolvedRecordMapper extends BaseMapper<TurntableInvolvedRecord> {

}