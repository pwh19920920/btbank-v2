package com.spark.bitrade.biz.impl;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.biz.ActivityRedpacketService;
import com.spark.bitrade.biz.ScheduleService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.SilkDataDist;
import com.spark.bitrade.repository.entity.ActivityRedPackManage;
import com.spark.bitrade.repository.entity.FinancialActivityJoinDetails;
import com.spark.bitrade.repository.service.FinancialActivityJoinDetailsService;
import com.spark.bitrade.service.SilkDataDistService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Slf4j
public class RedPacketTest {
    @Autowired
    private SilkDataDistService silkDataDistService;
    @Autowired
    ActivityRedpacketService activityRedpacketService;
    @Autowired
    private FinancialActivityJoinDetailsService financialActivityJoinDetailsService;
    @Autowired
    ScheduleService scheduleService;
    @Test
    public void getSystemConfigAccount(){
        SilkDataDist silkDataDist = silkDataDistService.findOne("RED_PACK_CONFIG","TOTAL_ACCOUNT_ID");

        System.out.println("@@@@@@@@@@@@"+ JSON.toJSONString(silkDataDist));
    }
    @Test
    public void grabeOrderReward(){
        //
        activityRedpacketService.processGrabOrderRedPack(360606L);
        activityRedpacketService.processGrabOrderRedPack(360606L);
        activityRedpacketService.processGrabOrderRedPack(360606L);
        activityRedpacketService.processGrabOrderRedPack(360606L);
        activityRedpacketService.processGrabOrderRedPack(360606L);
        activityRedpacketService.processGrabOrderRedPack(360606L);
        activityRedpacketService.processGrabOrderRedPack(360606L);
        activityRedpacketService.processGrabOrderRedPack(360606L);
        activityRedpacketService.processGrabOrderRedPack(360606L);
        activityRedpacketService.processGrabOrderRedPack(360606L);

        //System.out.println("@@@@@@@@@@@@"+ JSON.toJSONString(silkDataDist));
    }
    @Test
    public void ackRedPack(){
        activityRedpacketService.ackRedPack(1216672252905406465L);
    }

    @Test
    public void getRedPack(){
        Member member = new Member();
        member.setId(360606L);
        System.out.println("@@@@@@@@@@@@"+ JSON.toJSONString(activityRedpacketService.getRedPack(member,0)));
        System.out.println("@@@@@@@@@@@@"+ JSON.toJSONString(activityRedpacketService.getRedPack(member,1)));
        System.out.println("@@@@@@@@@@@@"+ JSON.toJSONString(activityRedpacketService.getRedPack(member,1)));
    }
    @Test
    public void getreCommandRedPack(){
        Member member = new Member();
        member.setId(69874L);
        member.setInviterId(69870L);
        for(int i =0 ;i<10;i++){
            recommand(member);
        }
    }
    public void recommand(Member member){
        System.out.println("@@@@@@@@@@@@"+ JSON.toJSONString(activityRedpacketService.processRecommendRedPack(member)));
    }
    @Test
    public void realseUnlock(){
        List<ActivityRedPackManage> list = activityRedpacketService.getRealseLockAmountActivity();
        for(ActivityRedPackManage activityRedPackManage : list){
            try{
                activityRedpacketService.realseLockAmount(activityRedPackManage);
            }catch (Exception e){
                e.printStackTrace();
                log.info("释放活动锁仓金额失败Id ：{}，name：{}，",activityRedPackManage.getId());
            }
        }
    }
    @Test
    public void recommandUnlock(){
        List<FinancialActivityJoinDetails> financialActivityJoinDetails = financialActivityJoinDetailsService.lambdaQuery()
                .eq(FinancialActivityJoinDetails::getType,0)
                .eq(FinancialActivityJoinDetails::getRecommendStatus,0)
                .gt(FinancialActivityJoinDetails::getReleaseProfitAmount,0)
                .isNotNull(FinancialActivityJoinDetails::getReleaseProfitTime)
                .last(" and release_profit_time <= now() limit 1000").list();
        System.out.println("@@@@@@@@@@@@"+ financialActivityJoinDetails.size());
        System.out.println("@@@@@@@@@@@@"+ JSON.toJSONString(financialActivityJoinDetails));
        FinancialActivityJoinDetails financialActivityJoinDetail  = financialActivityJoinDetailsService.getById(1209357041697624066L) ;
        scheduleService.doRecommendUnlockFinancialActivity(financialActivityJoinDetail);
    }


}
