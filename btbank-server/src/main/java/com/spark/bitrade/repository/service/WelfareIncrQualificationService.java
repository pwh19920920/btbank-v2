package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.WelfareIncrQualification;

/**
 * 增值福利参与资格(WelfareIncrQualification)表服务接口
 *
 * @author biu
 * @since 2020-04-08 14:16:33
 */
public interface WelfareIncrQualificationService extends IService<WelfareIncrQualification> {

    /**
     * 1、参与对象：仅参加过增值计划的账号可以购买
     * <p>
     * 2、可购份数：可购份数=曾经参加增值计划投入本金的额度/每份价值10000，只取整数，零头不算。（撤回不算份数）
     *
     * @param member m
     * @return count
     */
    Integer chances(Member member);

    /**
     * 统计机会
     *
     * @param memberId mid
     * @return nullable
     */
    Integer countTotal(Long memberId);

    /**
     * 扣除次数
     *
     * @param memberId mId
     * @return bool
     */
    boolean decrease(Long memberId);

    /**
     * 撤回次数
     *
     * @param memberId mId
     * @return bool
     */
    boolean refund(Long memberId);
}