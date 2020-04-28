package com.spark.bitrade.biz.impl;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.api.dto.ActivitiesDTO;
import com.spark.bitrade.api.dto.ActivitiesPrizeDTO;
import com.spark.bitrade.biz.support.TurntableGiveOutDispatcher;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.TurntablePrize;
import com.spark.bitrade.repository.entity.TurntableWinning;
import com.spark.bitrade.repository.service.TurntableWinningService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TurntableTests
 *
 * @author biu
 * @since 2020/1/10 11:38
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Slf4j
public class TurntableTests {
    @Autowired
    private TurntableGiveOutDispatcher giveOutDispatcher;
    @Autowired
    private TurntableWinningService winningService;

    /**
     * 测试中奖概率问题
     */
    @Test
    public void testActivitiesDraw() {

        List<TurntablePrize> prizes = new ArrayList<>();
        prizes.add(buildPrize("奖品1", 1, 30)); // 30%
        prizes.add(buildPrize("奖品2", 2, 10));
        prizes.add(buildPrize("奖品3", 3, 5));
        prizes.add(buildPrize("奖品4", 4, 3));
        prizes.add(buildPrize("奖品5", 5, 1));
        prizes.add(buildPrize("奖品6", 6, 0.9));
        prizes.add(buildPrize("奖品7", 7, 0.1));

        ActivitiesDTO dto = new ActivitiesDTO();
        dto.setPrizes(prizes);

        // 抽1000次
        List<ActivitiesPrizeDTO> draws = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            ActivitiesPrizeDTO draw = dto.draw();
            // System.out.println(draw);
            draws.add(draw);
        }

        Map<String, Integer> collect = draws.stream().collect(Collectors.groupingBy(r -> r.getName() + "," + r.getRate() + "%", Collectors.summingInt(r -> 1)));

        collect.forEach((k, v) -> {
            System.out.println(String.format("prize : %s -> count : %d", k, v));
        });
    }

    private TurntablePrize buildPrize(String name, int priority, double rate) {
        TurntablePrize prize = new TurntablePrize();
        prize.setId(0);
        prize.setName(name);
        prize.setPriority(priority);
        prize.setRate(rate);
        prize.setStock(9999);
        prize.setType("OBJECT");

        return prize;
    }

    @Test
    public void TestNoneRecord() {
        ActivitiesPrizeDTO prize = ActivitiesPrizeDTO.noneOf(100);
        Member member = new Member();
        member.setId(360397L);
        member.setRealName("12222");
        member.setMobilePhone("155");
        member.setUsername("123456");
        TurntableWinning winning = winningService.winning(2, member, ActivitiesPrizeDTO.noneOfEight(100));
        Long winId = winning.getId();
        giveOutDispatcher.dispatch(winning);
        System.out.println(JSON.toJSONString(winning));
    }
}
