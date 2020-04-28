package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.WelfareNewQualification;

/**
 * 新人福利参与资格(WelfareNewQualification)表服务接口
 *
 * @author biu
 * @since 2020-04-08 14:17:15
 */
public interface WelfareNewQualificationService extends IService<WelfareNewQualification> {

    /**
     * 1、参与对象：“新人福利包”仅限4月1日后注册的新矿工（BT矿池有转入记录）购买（4月1日前注册的老矿工不能购买）
     * <p>
     * 2、可购份数：
     * <p>
     * a. 4月1日后注册的新矿工自注册之日起7天内有1份购买资格，超过7天没有使用则该份资格失效
     * <p>
     * b. 4月1日后注册的新矿工每推荐一名自动实名认证的新矿工（BT矿池有转入记录）且新矿工购买一份新人福利包，<br/>
     * 推荐人可额外获得一份购买资格，推荐的新矿工再次重复购买不计算上级购买资格。<br/>
     * 推荐新矿工购买新人福利包，推荐人获得购买新人福利包的资格封顶10份。（撤回不算份数）<br/>
     *
     * @param member m
     * @return count
     */
    Integer chances(Member member);

    /**
     * 增加购买资格
     *
     * @param memberId 会员ID
     * @param subId    下级ID
     * @return bool
     */
    boolean increase(Long memberId, Long subId);

    /**
     * 扣除购买资格
     *
     * @param memberId 会员ID
     * @param refId    购买记录ID
     * @return bool
     */
    boolean decrease(Long memberId, String refId);

    /**
     * 撤回购买资格
     *
     * @param memberId 会员ID
     * @param refId    购买记录ID
     * @return
     */
    boolean refund(Long memberId, String refId);

    /**
     * 计算购买资格
     * <p>
     * 23点定时执行
     */
    void calculate();
}