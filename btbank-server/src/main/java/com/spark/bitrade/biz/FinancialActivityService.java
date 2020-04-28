package com.spark.bitrade.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.api.vo.FinancialActivityJoinDetailsVo;
import com.spark.bitrade.entity.Member;

import com.spark.bitrade.api.vo.FinancialActivityManageVo;
import com.spark.bitrade.repository.entity.FinancialActivityJoinDetails;
import com.spark.bitrade.repository.entity.FinancialActivityManage;
import com.spark.bitrade.web.bind.annotation.MemberAccount;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author shenzucai
 * @time 2019.12.21 12:59
 */
public interface FinancialActivityService {

    /**
     * 参加理财活动
     *
     * @param member
     * @param amount
     * @param activityId
     * @return true
     * @author shenzucai
     * @time 2019.12.21 13:01
     */
    FinancialActivityJoinDetails joinLock(Member member, BigDecimal amount, Long activityId, Integer purchaseNums);

    /**
     * 参加理财活动锁仓
     *
     * @param member
     * @param amount
     * @param activityId
     * @return true
     * @author shenzucai
     * @time 2019.12.21 13:01
     */
    FinancialActivityJoinDetails joinActivitiesLock(Member member, BigDecimal amount, Long activityId, Integer purchaseNums);

    /**
     * 撤销参加理财活动
     * @author shenzucai
     * @time 2019.12.21 13:01
     * @param member
     * @param lockDetailId
     * @return true
     */
    Boolean cancelLock(Member member, Long lockDetailId);
    /**
     * 撤销参加理财活动
     * @author shenzucai
     * @time 2019.12.21 13:01
     * @param member
     * @param lockDetailId
     * @return true
     */
    Boolean cancelActivityLock(Member member, Long lockDetailId);

    /**
     * 查询可以参加的理财产品
     * @author shenzucai
     * @time 2019.12.21 13:01
     * @param financialActivityManageVo
     * @return true
     */
    List<FinancialActivityManage> getAvailableActivities(FinancialActivityManageVo financialActivityManageVo);

    /**
     * 根据ID查询理财产品
     * @author mahao
     * @time 2019.12.21 13:01
     * @param activityId
     * @return true
     */
    FinancialActivityManage getFinancialActivityManage(Long activityId);
    /**
     * 我参加的理财分页
     * @author mahao
     * @time 2019.12.21 13:01
     * @param member
     * @param type
     * @param timeType
     * @param current
     * @param size
     * @return IPage<FinancialActivityJoinDetails>
     */
    IPage<FinancialActivityJoinDetails> getJoinActivitiesPage( Member member, int type, int timeType, Integer current, Integer size);

    /**
     * 根据活动ID查询是否参加过活动
     * @author mahao
     * @time 2019.12.21 13:01
     * @param activityId
     * @return true
     */
    Boolean alreadyJoinActivity(Member member, Long activityId,int purchaseNums,FinancialActivityManage financialActivityManage) ;

    /**
     * 我参加的理财活动不包含撤销的和已释放的
     * @author mahao
     * @time 2019.12.21 13:01
     * @param member
     * @return IPage<FinancialActivityJoinDetails>
     */
    List<FinancialActivityJoinDetails> getJoinActivities( Member member);

    BigDecimal getJoinActivitiesProfit(Member member, int type);

    BigDecimal getTotalLock(Member member);

    FinancialActivityJoinDetailsVo getActivityDetails(Long lockDetailId);

    /**
     * 创建大宗矿池的账号
     * @author mahao
     * @time 2019.12.21 13:01
     * @param memberId
     * @return IPage<FinancialActivityJoinDetails>
     */
    boolean createBtBankFinancialBalance(Long memberId);
}
