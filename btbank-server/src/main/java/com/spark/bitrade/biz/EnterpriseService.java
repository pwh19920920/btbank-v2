package com.spark.bitrade.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.api.vo.*;
import com.spark.bitrade.repository.entity.EnterpriseMinerTransaction;

import java.util.List;
import java.util.Optional;

/**
 * EnterpriseService
 *
 * @author biu
 * @since 2019/12/23 17:25
 */
public interface EnterpriseService {

    Optional<EnterpriseMinerVO> findByMemberId(Long memberId);

    ApplicationResultVO apply(Long memberId, ApplicationVO vo);

    ApplicationResultVO findApplication(Long memberId);

    boolean transfer(TransferVo vo);

    EnterpriseMinerTransactionsVO query(Long memberId, List<Integer> types, QueryVo vo);

    IPage<EnterpriseMinerTransaction> page(Long memberId, List<Integer> types, QueryVo vo);

    boolean isAvailableEnterpriseMiner(Long memberId);
}
