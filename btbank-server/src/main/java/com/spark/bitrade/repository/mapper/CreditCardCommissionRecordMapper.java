package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.CreditCardCommissionRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;


/**
 * <p>
 * 信用卡手续费记录 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2020-04-08
 */
public interface CreditCardCommissionRecordMapper extends BaseMapper<CreditCardCommissionRecord> {

    @Select("SELECT * FROM credit_card_commission_record r WHERE r.commission_amount-r.un_lock_amount>0 and member_id=#{memberId} order by r.create_time asc")
    List<CreditCardCommissionRecord> queryUnLockList(@Param("memberId") Long memberId);

    @Update("update credit_card_commission_record set un_lock_amount=un_lock_amount+#{releaseAmount},status=#{status},remark=#{remark} where id=#{id}")
    int unLock(@Param("id") Long id, @Param("releaseAmount") BigDecimal releaseAmount, @Param("status") int status, @Param("remark") String remark);

}
