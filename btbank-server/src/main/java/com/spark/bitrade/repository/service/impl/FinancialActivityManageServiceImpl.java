package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.dto.ActivityDTO;
import com.spark.bitrade.api.vo.FinancialActivityManageVo;
import com.spark.bitrade.repository.entity.FinancialActivityJoinDetails;
import com.spark.bitrade.repository.mapper.FinancialActivityJoinDetailsMapper;
import com.spark.bitrade.repository.mapper.FinancialActivityManageMapper;
import com.spark.bitrade.repository.entity.FinancialActivityManage;
import com.spark.bitrade.repository.service.FinancialActivityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 理财活动管理表(FinancialActivityManage)表服务实现类
 *
 * @author daring5920
 * @since 2019-12-21 11:49:44
 */
@Service("financialActivityManageService")
public class FinancialActivityManageServiceImpl extends ServiceImpl<FinancialActivityManageMapper, FinancialActivityManage> implements FinancialActivityManageService {

    @Override
    public List<FinancialActivityManage> getAvailableActivities(FinancialActivityManageVo financialActivityManageVo) {
        return baseMapper.getAvailableActivities(financialActivityManageVo);
    }

    @Override
    public List<ActivityDTO> listActivities() {
        return baseMapper.listActivities();
    }


}