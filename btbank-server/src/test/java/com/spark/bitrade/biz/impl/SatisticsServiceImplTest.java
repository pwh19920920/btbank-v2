package com.spark.bitrade.biz.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * * @author Administrator * @time 2019.12.16 15:31
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class SatisticsServiceImplTest {

    @Autowired
    SatisticsServiceImpl satisticsService;



    @Test
    public void statMinerOrderTotal() {
//        satisticsService.statMinerOrderTotal();
//        satisticsService.getTotal("2019-12-11");

        satisticsService.statEnterpriseMineTotal();
    }
}