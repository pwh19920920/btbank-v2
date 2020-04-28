package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.MemberExperienceLockRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

/**
 * <p>
 * 3月8号体验金锁仓记录 Mapper 接口
 * </p>
 *
 * @author qhliao
 * @since 2020-03-06
 */
@Mapper
public interface MemberExperienceLockRecordMapper extends BaseMapper<MemberExperienceLockRecord> {

    @Select("select * from member_experience_lock_record where member_id=#{memberId} limit 1")
    Optional<MemberExperienceLockRecord> findByMemberId(@Param("memberId") Long memberId);

}
