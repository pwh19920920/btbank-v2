package com.spark.bitrade.biz.impl;

import com.spark.bitrade.api.dto.OtcLimitDTO;
import com.spark.bitrade.biz.OtcLimitService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * OtcLimitServiceTest
 * <p>
 * !!!!!!
 * 运行时需要注释掉 TestDatabaseConfig 的 @Configuration
 * !!!!!!
 *
 * @author biu
 * @since 2019/12/5 11:19
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class OtcLimitServiceTest {

    @Autowired
    private OtcLimitService otcLimitService;

    @Test
    public void testForbid() {
        BigDecimal forbid = otcLimitService.forbidToWithdrawAndTransferOut(349441L);
        System.out.println(forbid);
    }

    @Test
    public void testLimitDTOValidOfYesterdayTenOClock() {
        // > 10点之前 统计昨天0点之后的订单
        // > 10点到16点之间  统计昨天12点之后的订单
        // > 16点之后 统计今天的订单

        // 昨天10点的订单
        OtcLimitDTO dto = new OtcLimitDTO();
        dto.setLimitTime(getYesterdayOfTime("10:00:00"));

        // 10点之前 统计昨天0点之后的订单 -> false
        Assert.assertFalse(dto.isValid(OtcLimitDTO.Range.TEN_BEFORE));
        // 10点到16点之间  统计昨天12点之后的订单 -> false
        Assert.assertFalse(dto.isValid(OtcLimitDTO.Range.TEN_TO_SIXTEEN));
        // > 16点之后 统计今天的订单
        Assert.assertFalse(dto.isValid(OtcLimitDTO.Range.SIXTEEN_AFTER));
    }

    @Test
    public void testLimitDTOValidOfYesterdayTwentyOClock() {
        // > 10点之前 统计昨天0点之后的订单
        // > 10点到16点之间  统计昨天12点之后的订单
        // > 16点之后 统计今天的订单

        // 昨天12点的订单
        OtcLimitDTO dto = new OtcLimitDTO();
        dto.setLimitTime(getYesterdayOfTime("12:00:01"));

        // 10点之前 统计昨天0点之后的订单 -> true
        Assert.assertTrue(dto.isValid(OtcLimitDTO.Range.TEN_BEFORE));
        // 10点到16点之间  统计昨天12点之后的订单 -> false
        Assert.assertTrue(dto.isValid(OtcLimitDTO.Range.TEN_TO_SIXTEEN));
        // > 16点之后 统计今天的订单
        Assert.assertFalse(dto.isValid(OtcLimitDTO.Range.SIXTEEN_AFTER));
    }

    @Test
    public void testLimitDTOValidOfSixteen() {
        // > 10点之前 统计昨天0点之后的订单
        // > 10点到16点之间  统计昨天12点之后的订单
        // > 16点之后 统计今天的订单

        // 昨天23点的订单
        OtcLimitDTO dto = new OtcLimitDTO();
        dto.setLimitTime(getYesterdayOfTime("23:00:00"));

        // 10点之前 统计昨天0点之后的订单 -> true
        Assert.assertTrue(dto.isValid(OtcLimitDTO.Range.TEN_BEFORE));
        // 10点到16点之间  统计昨天12点之后的订单 -> false
        Assert.assertTrue(dto.isValid(OtcLimitDTO.Range.TEN_TO_SIXTEEN));
        // > 16点之后 统计今天的订单 false
        Assert.assertFalse(dto.isValid(OtcLimitDTO.Range.SIXTEEN_AFTER));
    }

    @Test
    public void testLimitDTOValidOfToday() {
        // > 10点之前 统计昨天0点之后的订单
        // > 10点到16点之间  统计昨天12点之后的订单
        // > 16点之后 统计今天的订单

        // 昨天12点的订单
        OtcLimitDTO dto = new OtcLimitDTO();
        dto.setLimitTime(new Date());

        // 10点之前 统计昨天0点之后的订单 -> true
        Assert.assertTrue(dto.isValid(OtcLimitDTO.Range.TEN_BEFORE));
        // 10点到16点之间  统计昨天12点之后的订单 -> false
        Assert.assertTrue(dto.isValid(OtcLimitDTO.Range.TEN_TO_SIXTEEN));
        // > 16点之后 统计今天的订单 true
        Assert.assertTrue(dto.isValid(OtcLimitDTO.Range.SIXTEEN_AFTER));
    }

    @Test
    public void testLimitRange() {

        Assert.assertEquals(OtcLimitDTO.Range.of(getDateFrom("2019-12-05 09:59:59")), OtcLimitDTO.Range.TEN_BEFORE);

        Assert.assertNotEquals(OtcLimitDTO.Range.of(getDateFrom("2019-12-05 10:00:00")), OtcLimitDTO.Range.TEN_BEFORE);

        Assert.assertEquals(OtcLimitDTO.Range.of(getDateFrom("2019-12-05 10:00:00")), OtcLimitDTO.Range.TEN_TO_SIXTEEN);

        Assert.assertEquals(OtcLimitDTO.Range.of(getDateFrom("2019-12-05 16:00:00")), OtcLimitDTO.Range.TEN_TO_SIXTEEN);

        Assert.assertNotEquals(OtcLimitDTO.Range.of(getDateFrom("2019-12-05 16:00:00")), OtcLimitDTO.Range.SIXTEEN_AFTER);

        Assert.assertEquals(OtcLimitDTO.Range.of(getDateFrom("2019-12-05 16:00:01")), OtcLimitDTO.Range.SIXTEEN_AFTER);
    }

    private Date getDateFrom(String string) {
        try {
            return new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(string);
        } catch (ParseException e) {
            throw new RuntimeException("invalid date");
        }
    }

    private Date getYesterdayOfTime(String time) {
        try {
            Date date = new SimpleDateFormat("HH:mm:ss").parse(time);

            Calendar now = Calendar.getInstance();
            Calendar instance = Calendar.getInstance();
            instance.setTime(date);

            now.set(Calendar.HOUR_OF_DAY, instance.get(Calendar.HOUR_OF_DAY));
            now.set(Calendar.MINUTE, instance.get(Calendar.MINUTE));
            now.set(Calendar.SECOND, instance.get(Calendar.SECOND));
            now.set(Calendar.MILLISECOND, instance.get(Calendar.MILLISECOND));

            now.add(Calendar.DATE, -1);
            return now.getTime();

        } catch (ParseException e) {
            throw new RuntimeException("invalid date");
        }
    }
}
