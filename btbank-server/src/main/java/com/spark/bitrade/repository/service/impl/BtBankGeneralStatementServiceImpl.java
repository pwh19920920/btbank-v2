package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.BtBankGeneralStatement;
import com.spark.bitrade.repository.mapper.BtBankGeneralStatementMapper;
import com.spark.bitrade.repository.service.BtBankGeneralStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 总报表(统计数据表)(BtBankGeneralStatement)表服务实现类
 *
 * @author daring5920
 * @since 2019-12-16 11:19:04
 */
@Service("btBankGeneralStatementService")
public class BtBankGeneralStatementServiceImpl extends ServiceImpl<BtBankGeneralStatementMapper, BtBankGeneralStatement> implements BtBankGeneralStatementService {

    @Autowired
    private BtBankGeneralStatementMapper btBankGeneralStatementMapper;

    @Override
    public Boolean selectTotal(LocalDateTime startTime,LocalDate time) {
        BtBankGeneralStatement bt = btBankGeneralStatementMapper.selectNew();
        if (bt != null) {
            Date newDate = bt.getTime();
            LocalDateTime ldt = newDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            Duration d = Duration.between(ldt,startTime.minusDays(1));
            if (d.toHours()>23) {
                BtBankGeneralStatement btBankGeneralStatement = btBankGeneralStatementMapper.selectTotal(startTime.minusDays(1),startTime);
                btBankGeneralStatementMapper.insert(btBankGeneralStatement);
            }
        } else {
            //补全之前的数据
            LocalDate now = LocalDate.now();
            LocalDate ldt = now.minusDays(30);
            LocalDateTime localDateTime = startTime.minusDays(30);
            while (ldt.isBefore(now)) {
                BtBankGeneralStatement btBankGeneralStatement = btBankGeneralStatementMapper.selectTotal(localDateTime,localDateTime.plusDays(1));
                btBankGeneralStatementMapper.insert(btBankGeneralStatement);
                ldt = ldt.plusDays(1);
                localDateTime = localDateTime.plusDays(1);
            }
        }
        return true;
    }

    @Override
    public BtBankGeneralStatement selectNew() {
        return btBankGeneralStatementMapper.selectNew();
    }
}