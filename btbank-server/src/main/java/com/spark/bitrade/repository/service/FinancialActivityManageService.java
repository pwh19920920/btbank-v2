package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.api.dto.ActivityDTO;
import com.spark.bitrade.api.vo.FinancialActivityManageVo;
import com.spark.bitrade.repository.entity.FinancialActivityManage;

import java.util.List;

/**
 * 理财活动管理表(FinancialActivityManage)表服务接口
 *
 * @author daring5920
 * @since 2019-12-21 11:49:44
 */
public interface FinancialActivityManageService extends IService<FinancialActivityManage> {

    List<FinancialActivityManage> getAvailableActivities(FinancialActivityManageVo financialActivityManageVo);

    List<ActivityDTO> listActivities();
}