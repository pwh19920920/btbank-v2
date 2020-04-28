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
 * 新用户3月8之后体验金账户
 * </p>
 *
 * @author qhliao
 * @since 2020-03-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MemberExperienceWallet implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 可用余额
     */
    private BigDecimal balance;

    /**
     * 冻结余额
     */
    private BigDecimal frozenBalance;

    /**
     * 锁仓余额
     */
    private BigDecimal lockBalance;

    /**
     * 用户ID
     */
    private Long memberId;

    /**
     * 版本
     */
    private Integer version;

    /**
     * 币种id
     */
    private String coinId;

    /**
     * 是否锁定
     */
    private Integer isLock;

    /**
     * 启动提币
     */
    private Integer enabledOut;

    /**
     * 启动充值
     */
    private Integer enabledIn;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
