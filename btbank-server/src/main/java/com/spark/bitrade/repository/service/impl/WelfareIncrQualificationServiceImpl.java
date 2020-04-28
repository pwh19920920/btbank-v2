package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.WelfareIncrQualification;
import com.spark.bitrade.repository.mapper.WelfareIncrQualificationMapper;
import com.spark.bitrade.repository.service.WelfareIncrQualificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * 增值福利参与资格(WelfareIncrQualification)表服务实现类
 *
 * @author biu
 * @since 2020-04-08 14:16:33
 */
@Slf4j
@Service("welfareIncrQualificationService")
public class WelfareIncrQualificationServiceImpl extends ServiceImpl<WelfareIncrQualificationMapper, WelfareIncrQualification> implements WelfareIncrQualificationService {

    @Override
    public Integer chances(Member member) {
        Date now = Calendar.getInstance().getTime();
        WelfareIncrQualification q = getById(member.getId());
        // 初始化
        if (q == null) {
            q = new WelfareIncrQualification();
            q.setId(member.getId());
            q.setTotal(0);
            q.setSurplus(0);
            q.setCreateTime(now);
            save(q);
        }

        // 统计更新
        // 可购份数=曾经参加增值计划投入本金的额度/每份价值10000，只取整数，零头不算。（撤回不算份数）
        Integer integer = baseMapper.countByMemberId(member.getId());// Nullable
        if (integer != null && integer > q.getTotal()) {
            int diff = integer - q.getTotal();
            baseMapper.increase(member.getId(), q.getTotal(), diff);
            log.info("更新增值计划参与次数 [ member_id = {}, total = {}, increment = {} ]", member.getId(), q.getTotal(), diff);
            return getById(member.getId()).getSurplus();
        } else {
            return q.getSurplus();
        }
    }

    @Override
    public Integer countTotal(Long memberId) {
        return baseMapper.countByMemberId(memberId);
    }

    @Override
    public boolean decrease(Long memberId) {
        return retBool(baseMapper.decrease(memberId));
    }

    @Override
    public boolean refund(Long memberId) {
        return retBool(baseMapper.refund(memberId));
    }
}