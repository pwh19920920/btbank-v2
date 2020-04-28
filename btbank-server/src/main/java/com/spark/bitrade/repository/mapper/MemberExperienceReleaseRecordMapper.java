package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.MemberExperienceReleaseRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 3月8号体验金释放记录 Mapper 接口
 * </p>
 *
 * @author qhliao
 * @since 2020-03-06
 */
@Mapper
public interface MemberExperienceReleaseRecordMapper extends BaseMapper<MemberExperienceReleaseRecord> {

    @Select("SELECT member_id FROM member_experience_release_record WHERE release_type=2")
    List<Long> findOldMemberHasReturn();

}
