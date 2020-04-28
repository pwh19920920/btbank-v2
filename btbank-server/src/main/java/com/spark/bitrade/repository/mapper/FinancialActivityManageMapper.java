package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.api.dto.ActivityDTO;
import com.spark.bitrade.api.vo.FinancialActivityManageVo;
import org.apache.ibatis.annotations.Mapper;
import com.spark.bitrade.repository.entity.FinancialActivityManage;

import java.util.List;
import java.util.Map;

/**
 * 理财活动管理表(FinancialActivityManage)表数据库访问层
 *
 * @author daring5920
 * @since 2019-12-21 11:49:44
 */
@Mapper
public interface FinancialActivityManageMapper extends BaseMapper<FinancialActivityManage> {

    List<FinancialActivityManage> getAvailableActivities(FinancialActivityManageVo financialActivityManageVo);

    List<ActivityDTO> listActivities();

}