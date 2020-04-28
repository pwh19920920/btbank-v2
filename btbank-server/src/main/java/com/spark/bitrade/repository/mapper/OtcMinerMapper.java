package com.spark.bitrade.repository.mapper;

import com.spark.bitrade.api.dto.MemberOrderCountDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * OtcMinerMapper
 *
 * @author biu
 * @since 2019/12/11 17:38
 */
@Mapper
@Repository
public interface OtcMinerMapper {

    @Select("SELECT \n" +
            "  case\n" +
            "    d.`advertise_type` \n" +
            "    WHEN 1 \n" +
            "    then d.`customer_id` \n" +
            "    else d.`member_id` \n" +
            "  end as id,\n" +
            "  count(1) orders\n" +
            "FROM\n" +
            "  `otc_order` d \n" +
            "WHERE d.`status` IN (1, 2) GROUP by id")
    List<MemberOrderCountDTO> ordersInProgress();

    @Select("SELECT id, real_name FROM member m\n" +
            "WHERE m.`member_level` = 2 \n" +
            "  AND m.`certified_business_status` IN (2, 5, 6) \n" +
            "  AND m.`bank` IS NOT NULL")
    List<MemberOrderCountDTO> certifiedMembers();

    @Select("SELECT \n" +
            "  fm.`member_id` as id,\n" +
            "  m.`real_name` \n" +
            "FROM\n" +
            "  `finc_member_account` fm \n" +
            "  LEFT JOIN member m \n" +
            "    ON fm.`member_id` = m.`id` \n" +
            "WHERE fm.`member_type` = 1 ")
    List<MemberOrderCountDTO> innerMembers();

    @Select("SELECT \n" +
            "  count(fm.`member_id`) \n" +
            "FROM\n" +
            "  `finc_member_account` fm \n" +
            "  LEFT JOIN member m \n" +
            "    ON fm.`member_id` = m.`id` \n" +
            "WHERE fm.`member_type` = 1  and fm.`member_id` = #{memberId}")
    int chechInnerMember(Long memberId);
    @Select("select member_id  from otc_order where  order_sn =  #{refId}  and status = 0 order by cancel_time  desc  limit 1")
    Long getByOrderSn(String refId);
}
