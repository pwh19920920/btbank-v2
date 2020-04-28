package com.spark.bitrade.biz.impl;

import com.spark.bitrade.api.dto.MemberOrderCountDTO;
import com.spark.bitrade.biz.OtcMinerAutoService;
import com.spark.bitrade.repository.mapper.OtcMinerMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * OtcMinerOrderAutoProcessTests
 *
 * @author biu
 * @since 2019/12/11 17:43
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class OtcMinerOrderAutoProcessTests {

    @Autowired
    private OtcMinerMapper mapper;

    @Autowired
    private OtcMinerAutoService minerAutoService;

    @Test
    public void testMapperQuery() {
        List<MemberOrderCountDTO> dtos = mapper.ordersInProgress();
        for (MemberOrderCountDTO dto : dtos) {
            System.out.println(dto);
        }

        List<MemberOrderCountDTO> list = mapper.certifiedMembers();
        list.forEach(System.out::println);
    }

    @Test
    public void testGetMembersWithOrders() {
        List<MemberOrderCountDTO> certifiedMembersWithOrders = minerAutoService.getCertifiedMembersWithOrders();
        certifiedMembersWithOrders.forEach(System.out::println);
    }

    @Test
    public void testAutoProcess() {
        String s = minerAutoService.autoProcessWithTimout30min();
        System.out.println(s);
    }
}
