package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.WelfareIncrQualification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * 增值福利参与资格(WelfareIncrQualification)表数据库访问层
 *
 * @author biu
 * @since 2020-04-08 14:16:33
 */
@Mapper
@Repository
public interface WelfareIncrQualificationMapper extends BaseMapper<WelfareIncrQualification> {

    // Nullable
    @Select("SELECT FLOOR(SUM(d.`total_amount`) / 10000) FROM lock_coin_detail d WHERE d.`member_id` = #{memberId} AND d.`type`=6 AND d.`coin_unit`='BT'")
    Integer countByMemberId(@Param("memberId") Long memberId);

    /**
     * 增加购买机会
     *
     * @param id    id
     * @param total total
     * @param diff  diff
     * @return affected
     */
    @Update("UPDATE `welfare_incr_qualification` w SET w.`total` = w.`total` + #{diff}, w.`surplus` = w.`surplus` + #{diff}, w.`update_time` = NOW() WHERE w.`id` = #{id} AND w.`total` = #{total}")
    int increase(@Param("id") Long id, @Param("total") Integer total, @Param("diff") int diff);

    /**
     * 扣除次数
     *
     * @param memberId mid
     * @return affected
     */
    @Update("UPDATE `welfare_incr_qualification` w SET w.`surplus` = w.`surplus` - 1, w.`update_time` = NOW() WHERE w.`id` = #{id} AND w.`surplus` > 0")
    int decrease(@Param("id") Long memberId);

    /**
     * 撤回次数
     *
     * @param memberId mid
     * @return affected
     */
    @Update("UPDATE `welfare_incr_qualification` w SET w.`surplus` = w.`surplus` + 1, w.`update_time` = NOW() WHERE w.`id` = #{id} AND w.`surplus` < w.`total`")
    int refund(@Param("id") Long memberId);
}