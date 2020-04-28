package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.api.dto.FixOrderDto;
import com.spark.bitrade.repository.entity.OtcOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * (OtcOrder)表数据库访问层
 *
 * @author daring5920
 * @since 2019-11-28 17:43:20
 */
@Mapper
public interface OtcOrderMapper extends BaseMapper<OtcOrder> {


    List<Date> getNeedDispatchDateList();

    //@Select("select member_id from otc_order  where  advertise_type=1 and  `status` in (4,5) and date(release_time)=#{dispatchDate} group by member_id")
    List<Long> getNeedDispatchMemberIds();

    @Select("select IFNULL(sum(mt.amount),0) total_sale_reward from member_transaction mt LEFT JOIN otc_order oo on oo.order_sn = mt.ref_id \n" +
            " where mt.member_id =#{memberId} and mt.type=49 and oo.release_time >= #{beginTime} and  oo.release_time < #{endTime}")
    BigDecimal getTotalSaleReward(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime, @Param("memberId") Long memberId);


    //@Select("select * from otc_order  where member_id=#{memberId} and sale_reward_status != 1 and advertise_type=1 and `status` in (3,5) and date(release_time)=#{dispatchDate};")
    List<OtcOrder> getNeedDispatchOrdersOrder();

    @Update("update otc_order set sale_reward_status=#{newStatus} where id=#{orderId}")
    int updateOrderDispatchSaleRewardStatus(@Param("orderId") Long orderId, @Param("newStatus") int newStatus);

    @Select("select create_time from otc_order ORDER BY create_time limit 1")
    Date getEarliestDate();
    int queryOtcorderCnt();

    List<FixOrderDto> queryFixOrders();

    int updateOtcOrderStaus();

    Boolean updateFixOtcOrderStaus(@Param("fixOrderDto") FixOrderDto fixOrderDto);
}