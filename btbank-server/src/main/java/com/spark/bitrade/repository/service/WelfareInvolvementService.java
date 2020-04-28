package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.api.vo.WelfareLockedVo;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.WelfareInvolvement;

import java.math.BigDecimal;

/**
 * 福利包活动参与明细(WelfareInvolvement)表服务接口
 *
 * @author biu
 * @since 2020-04-08 14:15:53
 */
public interface WelfareInvolvementService extends IService<WelfareInvolvement> {


    /**
     * 总收益
     *
     * @param member m
     * @param type   t
     * @return r
     */
    BigDecimal getTotalProfit(Member member, Integer type);

    /**
     * 总锁仓
     * <p>
     * 未释放的锁仓
     *
     * @param member m
     * @param type   t
     * @return r
     */
    BigDecimal getTotalLock(Member member, Integer type);

    /**
     * 获取增值锁仓余额
     *
     * @param member m
     * @return vo
     */
    WelfareLockedVo getLockedBalance(Member member);
}