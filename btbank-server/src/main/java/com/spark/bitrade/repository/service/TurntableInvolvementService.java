package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.TurntableInvolvement;

/**
 * 参与记录表(TurntableInvolvement)表服务接口
 *
 * @author biu
 * @since 2020-01-08 13:56:22
 */
public interface TurntableInvolvementService extends IService<TurntableInvolvement> {

    /**
     * 计算抽奖机会并返回
     *
     * @param memberId 会员ID
     * @return number
     */
    Integer calculateChances(Long memberId);

    /**
     * 扣除抽奖机会
     *
     * @param memberId 会员ID
     * @return bool
     */
    boolean decrChances(Long memberId);
}