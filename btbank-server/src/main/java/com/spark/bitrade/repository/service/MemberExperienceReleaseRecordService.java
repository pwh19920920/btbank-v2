package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.MemberExperienceReleaseRecord;

import java.util.List;

/**
 * <p>
 * 3月8号体验金释放记录 服务类
 * </p>
 *
 * @author qhliao
 * @since 2020-03-06
 */
public interface MemberExperienceReleaseRecordService extends IService<MemberExperienceReleaseRecord> {

    List<Long> findOldMemberHasReturn();

}
