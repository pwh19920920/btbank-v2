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
 * 信用卡手续费记录
 * </p>
 *
 * @author qiliao
 * @since 2020-04-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CreditCardCommissionRecord implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 手续费金额
     */
    private BigDecimal commissionAmount;

    /**
     * 已解锁数量
     */
    private BigDecimal unLockAmount;

    /**
     * 状态{0:未解释,1:部分解锁,2:全部解锁}
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private String hashCode;

    private String refId;
}
