package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.RedPackExperienceGold;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * 红包体检金流水表(RedPackExperienceGold)表数据库访问层
 *
 * @author daring5920
 * @since 2019-12-08 10:44:35
 */
@Mapper
public interface RedPackExperienceGoldMapper extends BaseMapper<RedPackExperienceGold> {

    /**
     * 查询所有的红包锁仓金额
     *
     * @param memberId
     * @return
     */
    @Select("select  IFNULL(sum(lock_amount),0) as red_bag_lock_amount from red_pack_lock where member_id=#{memberId}")
    BigDecimal queryRedBagLockAmount(@Param("memberId") Long memberId);

    Boolean saveGetId(@Param("redPackExperienceGold") RedPackExperienceGold redPackExperienceGold);
}