package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.api.dto.ActivitiesPrizeDTO;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.TurntableWinning;

import java.util.List;

/**
 * 中奖记录(TurntableWinning)表服务接口
 *
 * @author biu
 * @since 2020-01-08 13:56:46
 */
public interface TurntableWinningService extends IService<TurntableWinning> {

    boolean updateContact(Long id, String username, String mobile);

    List<TurntableWinning> getWinnings(Integer actId);

    List<TurntableWinning> getWinnings(Integer actId, Long memberId);

    boolean confirmReceived(Long id);

    TurntableWinning winning(Integer actId, Member member, ActivitiesPrizeDTO prize);
}