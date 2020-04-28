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
 * 3月8号体验金释放记录
 * </p>
 *
 * @author qhliao
 * @since 2020-03-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MemberExperienceReleaseRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long memberId;

    /**
     * 释放金额
     */
    private BigDecimal releaseAmount;

    /**
     * 币种
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
     * {1:新用户体验金释放,2:老用户推荐佣金释放}
     */
    private Integer releaseType;
    /**
     * 累计收益
     */
    private BigDecimal totalIncome;
    /**
     * 邀请人
     */
    private Long inviterId;
}
