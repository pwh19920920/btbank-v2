package com.spark.bitrade.biz.impl;

import com.spark.bitrade.biz.MinerRebateCheckService;
import com.spark.bitrade.util.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * MinerRebateCheckServiceTests
 *
 * @author biu
 * @since 2019/12/11 9:59
 */
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//@RunWith(SpringRunner.class)
public class MinerRebateCheckServiceTests {

    @Autowired
    private MinerRebateCheckService checkService;

    @Test
    public void testCheck() {
        checkService.checkRebate(DateUtils.parseDatetime("2019-12-01 00:00:00"));
    }
}
