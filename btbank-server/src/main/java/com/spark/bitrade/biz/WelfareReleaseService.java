package com.spark.bitrade.biz;

import com.spark.bitrade.repository.entity.WelfareInvolvement;

/**
 * WelfareReleaseService
 *
 * @author Archx[archx@foxmail.com]
 * @since 2020/4/13 11:31
 */
public interface WelfareReleaseService {

    /**
     * 释放本金
     *
     * @param involvement 参与记录
     */
    void principal(WelfareInvolvement involvement);

    /**
     * 释放收益
     *
     * @param involvement 参与记录
     */
    void interest(WelfareInvolvement involvement);

    /**
     * 释放直推
     *
     * @param involvement 参与记录
     */
    void invite(WelfareInvolvement involvement);

    /**
     * 释放金牌
     *
     * @param involvement 参与记录
     */
    void gold(WelfareInvolvement involvement);
}
