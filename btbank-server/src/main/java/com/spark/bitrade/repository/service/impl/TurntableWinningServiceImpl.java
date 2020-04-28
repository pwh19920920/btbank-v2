package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.dto.ActivitiesPrizeDTO;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.repository.entity.TurntableWinning;
import com.spark.bitrade.repository.mapper.TurntableWinningMapper;
import com.spark.bitrade.repository.service.TurntableWinningService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 中奖记录(TurntableWinning)表服务实现类
 *
 * @author biu
 * @since 2020-01-08 13:56:46
 */
@Service("turntableWinningService")
public class TurntableWinningServiceImpl extends ServiceImpl<TurntableWinningMapper, TurntableWinning> implements TurntableWinningService {

    @Override
    public boolean updateContact(Long id, String username, String mobile) {
        UpdateWrapper<TurntableWinning> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .set("contact_name", username)
                .set("contact_phone", mobile)
                .set("update_time", new Date());
        return update(wrapper);
    }

    @Override
    public List<TurntableWinning> getWinnings(Integer actId) {
        LambdaQueryWrapper<TurntableWinning> wrapper = new LambdaQueryWrapper<TurntableWinning>()
                .eq(actId != null && actId > 0, TurntableWinning::getActId, actId)
                .orderByDesc(TurntableWinning::getCreateTime);

        return page(new Page<>(1, 50), wrapper).getRecords();
    }

    @Override
    public List<TurntableWinning> getWinnings(Integer actId, Long memberId) {
        LambdaQueryWrapper<TurntableWinning> wrapper = new LambdaQueryWrapper<TurntableWinning>()
                .eq(actId != null && actId > 0, TurntableWinning::getActId, actId)
                .eq(TurntableWinning::getMemberId, memberId).orderByDesc(TurntableWinning::getCreateTime);

        return list(wrapper);
    }

    @Override
    public boolean confirmReceived(Long id) {
        UpdateWrapper<TurntableWinning> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .set("state", 2)
                .set("update_time", new Date());
        return update(wrapper);
    }

    @Override
    public TurntableWinning winning(Integer actId, Member member, ActivitiesPrizeDTO prize) {
        Long id = IdWorker.getId();

        TurntableWinning winning = new TurntableWinning();
        winning.setId(id);
        winning.setActId(actId);

        winning.setMemberId(member.getId());
        winning.setRealName(member.getRealName());
        winning.setUsername(member.getUsername());
        winning.setMobilePhone(member.getMobilePhone());

        winning.setPrizeId(prize.getId());
        winning.setPrizeName(prize.getName());
        winning.setPrizeAmount(prize.getAmount());
        winning.setPrizeImage(prize.getImage());
        winning.setPrizeType(prize.getType());
        winning.setPriority(prize.getPriority());

        winning.setState(0);
        winning.setCreateTime(new Date());

        if (save(winning)) {
            return winning;
        }

        throw new MessageCodeException(CommonMsgCode.FAILURE);
    }
}