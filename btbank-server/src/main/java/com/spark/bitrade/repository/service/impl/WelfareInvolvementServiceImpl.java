package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.vo.WelfareLockedVo;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.WelfareInvolvement;
import com.spark.bitrade.repository.mapper.WelfareInvolvementMapper;
import com.spark.bitrade.repository.service.WelfareInvolvementService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 福利包活动参与明细(WelfareInvolvement)表服务实现类
 *
 * @author biu
 * @since 2020-04-08 14:15:53
 */
@Service("welfareInvolvementService")
public class WelfareInvolvementServiceImpl extends ServiceImpl<WelfareInvolvementMapper, WelfareInvolvement> implements WelfareInvolvementService {

    @Override
    public BigDecimal getTotalProfit(Member member, Integer type) {
        LambdaQueryWrapper<WelfareInvolvement> query = new LambdaQueryWrapper<WelfareInvolvement>()
                .eq(WelfareInvolvement::getActType, type)
                .eq(WelfareInvolvement::getMemberId, member.getId())
                .eq(WelfareInvolvement::getStatus, 0)
                .ge(WelfareInvolvement::getReleaseStatus, 2);

        BigDecimal profit = BigDecimal.ZERO;
        for (WelfareInvolvement involvement : list(query)) {
            profit = profit.add(involvement.getEarningReleaseAmount());
        }
        return profit;
    }

    @Override
    public BigDecimal getTotalLock(Member member, Integer type) {
        LambdaQueryWrapper<WelfareInvolvement> query = new LambdaQueryWrapper<WelfareInvolvement>()
                .eq(WelfareInvolvement::getActType, type)
                .eq(WelfareInvolvement::getMemberId, member.getId())
                .eq(WelfareInvolvement::getStatus, 0)
                .eq(WelfareInvolvement::getReleaseStatus, 0);

        BigDecimal total = BigDecimal.ZERO;
        for (WelfareInvolvement involvement : list(query)) {
            total = total.add(involvement.getAmount());
        }
        return total;
    }

    @Override
    public WelfareLockedVo getLockedBalance(Member member) {
        LambdaQueryWrapper<WelfareInvolvement> query = new LambdaQueryWrapper<WelfareInvolvement>()
                .eq(WelfareInvolvement::getMemberId, member.getId())
                .eq(WelfareInvolvement::getStatus, 0)
                .eq(WelfareInvolvement::getReleaseStatus, 0);

        BigDecimal newLocked = BigDecimal.ZERO;
        BigDecimal incrLocked = BigDecimal.ZERO;

        for (WelfareInvolvement involvement : list(query)) {
            if (involvement.getActType() == 0) {
                newLocked = newLocked.add(involvement.getAmount());
            } else {
                incrLocked = incrLocked.add(involvement.getAmount());
            }
        }
        return new WelfareLockedVo(newLocked, incrLocked);
    }
}