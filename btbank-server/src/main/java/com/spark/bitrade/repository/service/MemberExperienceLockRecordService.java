package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.MemberExperienceLockRecord;

/**
 * <p>
 * 3月8号体验金锁仓记录 服务类
 * </p>
 *
 * @author qhliao
 * @since 2020-03-06
 */
public interface MemberExperienceLockRecordService extends IService<MemberExperienceLockRecord> {

    boolean lockRecordExistByMemberId(Long memberId);


    MemberExperienceLockRecord findByMemberId(Long memberId);
}
