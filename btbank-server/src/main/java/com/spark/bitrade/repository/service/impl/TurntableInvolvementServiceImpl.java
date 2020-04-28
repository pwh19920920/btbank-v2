package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.dto.ActivitiesChanceDTO;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.repository.entity.TurntableInvolvement;
import com.spark.bitrade.repository.mapper.TurntableInvolvementMapper;
import com.spark.bitrade.repository.mapper.TurntableMapper;
import com.spark.bitrade.repository.service.TurntableInvolvementService;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 参与记录表(TurntableInvolvement)表服务实现类
 *
 * @author biu
 * @since 2020-01-08 13:56:22
 */
@Service("turntableInvolvementService")
public class TurntableInvolvementServiceImpl extends ServiceImpl<TurntableInvolvementMapper, TurntableInvolvement> implements TurntableInvolvementService {

    private static Date DEFAULT_REGISTRATION_TIME = DateUtils.parseDatetime("2020-01-16 00:00:00");
    private TurntableMapper turntableMapper;
    private BtBankConfigService configService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer calculateChances(Long memberId) {
        Date date = configService.getConfig(BtBankSystemConfig.TURNTABLE_MINER_REGISTRATION_TIME,
                (v) -> DateUtils.parseDatetime(v.toString()),
                DEFAULT_REGISTRATION_TIME);

        // 实时统计
        // not supported token 'HAVING'
        List<ActivitiesChanceDTO> dtos = turntableMapper.findMinerBalance(memberId, date);
        int size = dtos.stream().filter(ActivitiesChanceDTO::isAvailable).map(e -> 1).reduce(0, Integer::sum);

        TurntableInvolvement involvement = getById(memberId);

        // 初始化
        if (involvement == null) {
            involvement = new TurntableInvolvement();
            involvement.setId(memberId);
            involvement.setTotal(size);
            involvement.setSurplus(size);
            involvement.setCreateTime(new Date());

            save(involvement);

            return involvement.getSurplus();
        } else {
            int diff = size - involvement.getTotal();
            // 有变化
            if (diff > 0) {
                // 增加
                if (baseMapper.increment(memberId, diff, involvement.getTotal()) > 0) {
                    return involvement.getSurplus() + diff;
                }
                throw BtBankMsgCode.TURNTABLE_CHANCE_PROCESS_FAILED.asException();
            }
            return involvement.getSurplus();
        }

    }

    @Override
    public boolean decrChances(Long memberId) {
        return baseMapper.decrement(memberId, 1) > 0;
    }

    // ------------------------------
    // > S E T T E R S
    // ------------------------------

    @Autowired
    public void setTurntableMapper(TurntableMapper turntableMapper) {
        this.turntableMapper = turntableMapper;
    }

    @Autowired
    public void setConfigService(BtBankConfigService configService) {
        this.configService = configService;
    }
}