package com.spark.bitrade.biz.impl;

import com.spark.bitrade.biz.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * @author: Zhong Jiang
 * @date: 2020-03-06 17:13
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Slf4j
public class MemberExperienceWalletTests {

    @Autowired
    private ScheduleService scheduleService;

    @Test
    public void testOrder(){
        scheduleService.autoReleaseProfit();
    }


}
