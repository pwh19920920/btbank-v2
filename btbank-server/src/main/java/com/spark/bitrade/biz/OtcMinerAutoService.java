package com.spark.bitrade.biz;

import com.spark.bitrade.api.dto.MemberOrderCountDTO;

import java.util.List;

/**
 * OtcMinerAutoService
 *
 * @author biu
 * @since 2019/12/11 16:27
 */
public interface OtcMinerAutoService {

    String autoProcessWithTimout30min();

    List<MemberOrderCountDTO> getCertifiedMembersWithOrders();
}
