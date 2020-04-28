package com.spark.bitrade.biz;

import com.spark.bitrade.repository.entity.FinancialActivityJoinDetails;

/**
 * @author shenzucai
 * @time 2019.10.24 16:35
 */
public interface ScheduleService {

    /**
     * 解锁资产
     * @author shenzucai
     * @time 2019.10.24 16:41
     * @return true
     */
    Boolean unLockAssert();


    /**
     * 自动派单
     * 作为派单定时任务，将24小时无人抢单的订单，根据规则分派一个符合条件的矿工（防止订单积压）。
     * 矿工的【矿池可用】必须大于等于【订单金额】，没有符合条件的失败，等待下次执行
     * 未完成订单最少的
     * 24小时内抢单次数最少的
     * 24小时抢单总金额最少的
     *
     * @author shenzucai
     * @time 2019.10.24 16:51
     * @return true
     */
    Boolean autoDispatch();

    /**
     * 理财活动状态变更
     * @author shenzucai
     * @time 2019.12.21 15:52
     * @param
     * @return true
     */
    Boolean autoUpdateActivity();

    /**
     * 理财活动利息和本金解锁
     * @author shenzucai
     * @time 2019.12.21 15:52
     * @param
     * @return true
     */
    Boolean autoProfitUnlock();

    Boolean recommendUnlock();
    Boolean doRecommendUnlockFinancialActivity(FinancialActivityJoinDetails financialActivityJoinDetail);

    /**
     * 3月8日 累计挖矿收益 600BT 一次性释放
     */
    Boolean autoReleaseProfit();

}
