package com.spark.bitrade.biz;

import com.spark.bitrade.api.vo.ActivitiesCarouselVO;
import com.spark.bitrade.api.vo.ActivitiesDrawVO;
import com.spark.bitrade.api.vo.ActivitiesVO;
import com.spark.bitrade.api.vo.ActivitiesWinningVO;
import com.spark.bitrade.entity.Member;

import java.util.List;

/**
 * TurntableService
 *
 * @author biu
 * @since 2020/1/8 9:59
 */
public interface TurntableService {

    /**
     * 活动是否开放
     *
     * @return bool
     */
    boolean activityIsOpen();

    /**
     * 获取活动配置
     *
     * @return activity
     */
    ActivitiesVO getActivities();

    /**
     * 轮播信息
     *
     * @param actId 活动ID
     * @return list
     */
    List<ActivitiesCarouselVO> carousel(Integer actId);

    /**
     * 抽奖
     *
     * @param actId  活动ID
     * @param member 会员
     * @return vo
     */
    ActivitiesDrawVO draw(Integer actId, Member member);

    /**
     * 获取机会次数
     *
     * @param memberId 会员ID
     * @return number
     */
    Integer getChances(Long memberId);

    /**
     * 兑换奖品
     *
     * @param id       中奖记录ID
     * @param memberId 会员ID
     * @param username 收货人
     * @param mobile   收货人电话
     * @return bool
     */
    boolean exchangePrize(Long id, Long memberId, String username, String mobile);

    /**
     * 中奖记录
     *
     * @param actId    活动ID
     * @param memberId 会员ID
     * @return records
     */
    List<ActivitiesWinningVO> getWinnings(Integer actId, Long memberId);

    /**
     * 确认收货
     *
     * @param winId    中奖记录ID
     * @param memberId 会员ID
     * @return bool
     */
    boolean confirmReceived(Long winId, Long memberId);

    /**
     * 手动发放奖励
     *
     * @param winId winId
     */
    void giveOut(Long winId);
}
