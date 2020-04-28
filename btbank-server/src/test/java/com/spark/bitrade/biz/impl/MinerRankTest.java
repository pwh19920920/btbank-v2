package com.spark.bitrade.biz.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.api.vo.FinancialActivityManageVo;
import com.spark.bitrade.biz.FinancialActivityService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.FinancialActivityJoinDetails;
import com.spark.bitrade.repository.entity.FinancialActivityManage;
import com.spark.bitrade.repository.entity.MemberAssetStatistics;
import com.spark.bitrade.repository.entity.RankRewardTransaction;
import com.spark.bitrade.repository.service.MemberAssetStatisticsService;
import com.spark.bitrade.repository.service.RankRewardTransactionService;
import com.spark.bitrade.util.DateUtil;
import com.spark.bitrade.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mahao on 2019/12/18.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Slf4j
public class MinerRankTest {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private FinancialActivityService financialActivityService;
    @Autowired
    private RankRewardTransactionService rankRewardTransactionService;
    @Autowired
    private MemberAssetStatisticsService memberAssetStatisticsService;
    @Test
    public void testCheck() {
        Date queryDate =   DateUtil.addDay(new Date(),-1);
        System.out.println(queryDate);
        RankRewardTransaction rankRewardTransaction = new RankRewardTransaction();
        rankRewardTransaction.setCreateTime(DateUtil.addDay(new Date(),-1));
        rankRewardTransaction.setRewardType(1);
        System.out.println( JSON.toJSONString(rankRewardTransactionService.getRankListByType(rankRewardTransaction)));
    }

    @Test
    public void testTestTime() {
       Date weekStart = DateUtils.getWeekSstart();
       Date weekEnd = DateUtils.getWeekEnd();
       Date monthStart = DateUtils.getMonthStart();
       Date monthEnd = DateUtils.getMonthEnd();
       Date yearStart = DateUtils.getMonthStart();
       Date yearEnd = DateUtils.getMonthStart();
       System.out.println( weekStart);
       System.out.println( weekEnd);
        System.out.println( monthStart);
        System.out.println( monthEnd);
        System.out.println( DateUtils.subDate(new Date(),10));
    }
    @Test
    public void testActivites() {
        FinancialActivityManageVo financialActivityManageVo = new FinancialActivityManageVo();
        financialActivityManageVo.setType(0);
        financialActivityManageVo.setMemberId(306464L);
        Member member = new Member();
        member.setId(306464L);
        List<Long> josinActivityIds = new ArrayList<>();
        List<FinancialActivityJoinDetails> josinActivities =financialActivityService.getJoinActivities(member);
        for (FinancialActivityJoinDetails financialActivityJoinDetails:josinActivities){
            josinActivityIds.add(financialActivityJoinDetails.getActivityId());
        }
        System.out.println(JSON.toJSONString(josinActivities));
        System.out.println(JSON.toJSONString(josinActivityIds));
        if(josinActivityIds.size()>0){
            financialActivityManageVo.setJoinids(josinActivityIds);
        }
        System.out.println(JSON.toJSONString(financialActivityService.getAvailableActivities(financialActivityManageVo)));
    }
    @Test
    public void testActivitesProfite() {
        Member member = new Member();
        member.setId(306464L);
        System.out.println(JSON.toJSONString(financialActivityService.getJoinActivitiesProfit(member,0)));
        System.out.println(JSON.toJSONString(financialActivityService.getJoinActivitiesProfit(member,1)));
        System.out.println(JSON.toJSONString(financialActivityService.getJoinActivitiesProfit(member,2)));
        System.out.println(JSON.toJSONString(financialActivityService.getJoinActivitiesProfit(member,3)));
    }
    @Test
    public void testgetAcivity() {
        /*int activityId = 20;
        Member member = new Member();
        member.setId(306464L);
        if(activityId ==null &&activityId.equals(0)){
            throw new BtBankException(BtBankMsgCode.FIND_ACTIVITY_FAILED);
        }
        //防止重复提交
        if(redisTemplate.hasKey("ACTIVITI:ACTIVITYID:"+activityId+":memberId"+member.getId())){
            throw new BtBankException(BtBankMsgCode.ALREADY_ATTEND_ACTIVITY);
        }else{
            this.redisTemplate.opsForValue().set("ACTIVITI:ACTIVITYID:"+activityId+":memberId"+member.getId(), 1, 5,  TimeUnit.SECONDS);
        }
        FinancialActivityManage financialActivityManage = financialActivityService.getFinancialActivityManage(activityId);
        if (Objects.isNull(financialActivityManage)) {
            throw new BtBankException(BtBankMsgCode.FIND_ACTIVITY_FAILED);
        }
        if(purchaseNums>financialActivityManage.getUpSinglePurchase()){
            throw new BtBankException(BtBankMsgCode.ACTIVITY_OVER_MAX_LIMIT);
        }
        if(purchaseNums>financialActivityManage.getRemainPurchaseNums()){
            throw new BtBankException(BtBankMsgCode.ACTIVITY_OVER_MAX);
        }
        //查询是否已经购买
        if(financialActivityService.alreadyJoinActivity(member,activityId,purchaseNums,financialActivityManage)){
            throw new BtBankException(BtBankMsgCode.ALREADY_ATTEND_ACTIVITY);
        }
        Date current = new Date();
        if(financialActivityManage.getCreateTime().after(current)){
            throw new BtBankException(BtBankMsgCode.ACTIVITY_NOT_START);
        }
        if(financialActivityManage.getFinalizeTime().before(current)){
            throw new BtBankException(BtBankMsgCode.ACTIVITY_END);
        }
        FinancialActivityJoinDetails financialActivityJoinDetails = financialActivityService.joinLock(member,financialActivityManage.getPerAmount().multiply(new BigDecimal(purchaseNums)),activityId,purchaseNums);
       */ /* FinancialActivityManage financialActivityManage = financialActivityService.getFinancialActivityManage(12L);
        System.out.println(JSON.toJSONString(financialActivityManage));
        Member member = new Member();
        member.setId(306464L);
        BigDecimal totalProfite = financialActivityService.getJoinActivitiesProfit( member, 3);
        BigDecimal totalLock = financialActivityService.getTotalLock(member);
        Map map = new HashMap();
        map.put("totalProfite",totalProfite);
        map.put("totalLock",totalLock);
        System.out.println(JSON.toJSONString(map)); */
        //FinancialActivityManageVo financialActivityManageVo = new FinancialActivityManageVo();
        //System.out.println(JSON.toJSONString(financialActivityService.getAvailableActivities(financialActivityManageVo)));
    }
    @Test
    public void joinedAcivity() {
        Member member = new Member();
        member.setId(280597L);
        int type =0;
        int timeType =0;
        int current = 0 ;
        int size = 20;
        System.out.println(JSON.toJSONString( financialActivityService.getJoinActivitiesPage( member, type, timeType, current, size)));

    }
    @Test
    public void jobAcivity() {

        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime startTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endTime =  LocalDateTime.now();
        MemberAssetStatistics memberAssetStatistics = new MemberAssetStatistics();
        ZonedDateTime startDate = startTime.atZone(zoneId);
        ZonedDateTime endDate = endTime.atZone(zoneId);
        memberAssetStatistics.setStartTime( Date.from(startDate.toInstant()));
        memberAssetStatistics.setEndTime(Date.from(endDate.toInstant()));

        List<MemberAssetStatistics>  lst = memberAssetStatisticsService.queryUserAsset(memberAssetStatistics);
        if(lst.size()>0){
           memberAssetStatisticsService.deleteAll();
            memberAssetStatisticsService.insertBath(lst);
        }
    }
    @Test
    public void joinedallAcivity() {
        int type = 2;
        int timeType = 0;
        int current =1;
        int size =20;
       // Member member = new Member();
       // member.setId(280597L);
       // System.out.println(JSON.toJSONString( financialActivityService.getJoinActivitiesPage( member, type, timeType, current, size)));

        System.out.println(JSON.toJSONString( financialActivityService.getActivityDetails(1209361163108790274L)));
    }



}
