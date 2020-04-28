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
 * 3月8号体验金锁仓记录
 * </p>
 *
 * @author qhliao
 * @since 2020-03-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MemberExperienceLockRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long memberId;

    /**
     * 转入挖矿的金额(做个记录)
     */
    private BigDecimal inAmount;

    /**
     * 锁仓金额
     */
    private BigDecimal lockAmount;

    /**
     * 锁仓币种
     */
    private String coin;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 已释放金额
     */
    private BigDecimal releasedAmount;

}
