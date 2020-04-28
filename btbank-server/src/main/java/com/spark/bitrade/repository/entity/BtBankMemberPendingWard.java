package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用户直推待领取奖励记录
 * </p>
 *
 * @author qhliao
 * @since 2020-03-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BtBankMemberPendingWard implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 直推奖励金额
     */
    private BigDecimal wardAmount;

    /**
     * 状态{0:未领取,1:已领取}
     */
    private Integer status;

    /**
     * 直推矿工ID
     */
    private Long childId;

    /**
     * 领取时间
     */
    private Date receiveTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 奖励类型 0:直推佣金 1:大宗挖矿直推佣金 2:新人福利挖矿直推佣金 3:增值福利挖矿直推佣金
     */
    private Integer type;

    /**
     * 流水id
     */
    private Long txId;

    private String comments;
}
