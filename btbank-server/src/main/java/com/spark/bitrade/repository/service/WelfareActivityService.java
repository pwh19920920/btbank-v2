package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.WelfareActivity;
import com.spark.bitrade.repository.entity.WelfareInvolvement;

import java.util.List;

/**
 * 福利包活动(WelfareActivity)表服务接口
 *
 * @author biu
 * @since 2020-04-08 14:14:43
 */
public interface WelfareActivityService extends IService<WelfareActivity> {

    /**
     * 根据类型查询最新的活动
     *
     * @param type 0：新人福利包 1：增值福利包
     * @return activity
     */
    WelfareActivity findTheLatest(Integer type);

    /**
     * 获取全部福利包产品, 创建时间倒序排列
     *
     * @param type 0：新人福利包 1：增值福利包
     * @return list
     */
    List<WelfareActivity> findAllByType(Integer type);

    /**
     * 统计更新剩余次数并返回
     *
     * @param type   0：新人福利包 1：增值福利包
     * @param member 会员信息
     * @return count
     */
    Integer chances(Integer type, Member member);

    /**
     * 购买福利包
     *
     * @param id     福利包ID
     * @param number 数量
     * @param member 会员信息
     * @return
     */
    List<WelfareInvolvement> buy(Integer id, Integer number, Member member);

    /**
     * 撤回福利包
     *
     * @param id     福利包ID
     * @param member 会员信息
     * @return
     */
    WelfareInvolvement refund(Long id, Member member);

}