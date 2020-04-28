package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.WelfareInvolvement;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 福利包活动参与明细(WelfareInvolvement)表数据库访问层
 *
 * @author biu
 * @since 2020-04-08 14:15:53
 */
@Mapper
@Repository
public interface WelfareInvolvementMapper extends BaseMapper<WelfareInvolvement> {

}