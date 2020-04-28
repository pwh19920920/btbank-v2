package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.dto.ActivitiesDTO;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.repository.entity.TurntableActivities;
import com.spark.bitrade.repository.entity.TurntablePrize;
import com.spark.bitrade.repository.mapper.TurntableActivitiesMapper;
import com.spark.bitrade.repository.service.TurntableActivitiesService;
import com.spark.bitrade.repository.service.TurntablePrizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 活动表(TurntableActivities)表服务实现类
 *
 * @author biu
 * @since 2020-01-08 13:56:07
 */
@Service("turntableActivitiesService")
public class TurntableActivitiesServiceImpl extends ServiceImpl<TurntableActivitiesMapper, TurntableActivities> implements TurntableActivitiesService {

    private TurntablePrizeService prizeService;

    @Override
    public TurntableActivities getInProgressOrLatestActivities() {
        // 查询正在进行中的活动, 按创建时间升序排序 headOne
        TurntableActivities inProcess = baseMapper.getInProcess();
        if (inProcess != null) {
            return inProcess;
        }

        // 获取开始时间最近的一条
        return baseMapper.getTheLatest();
    }

    @Override
    public ActivitiesDTO findById(Integer id) {
        TurntableActivities act = getById(id);
        if (act == null) {
            throw BtBankMsgCode.TURNTABLE_ACTIVITY_NOT_FOUND.asException();
        }
        List<TurntablePrize> prizes = prizeService.getPrizes(id);

        ActivitiesDTO dto = new ActivitiesDTO();
        dto.setId(id);
        dto.setStart(act.getStartTime().getTime());
        dto.setEnd(act.getEndTime().getTime());

        dto.setPrizes(prizes);

        return dto;
    }

    @Autowired
    public void setPrizeService(TurntablePrizeService prizeService) {
        this.prizeService = prizeService;
    }
}