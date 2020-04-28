package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 广告实体类
 *
 * @author qiuyuanjie
 * @date 2020-02-29
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class Advertise implements Serializable {

    @TableId
    private Long id;
    /**
     * 广告类型:0:买入 1:卖出
     */
    private Integer advertiseType;
    /**
     * 自动回复
     */
    private Integer auto;
    /**
     * 自动回复内容
     */
    private String autoword;
    /**
     *
     */
    private String coinUnit;
    /**
     * 广告创建时间
     */
    private Date createTime;
    /**
     * 交易中数量
     */
    private BigDecimal dealAmount;
    /**
     * 广告级别:0=普通/1=优质
     */
    private Integer level;

    private String limitMoney;
    /**
     * 最高单笔交易额
     */
    private BigDecimal maxLimit;
    /**
     * 最低单笔交易额
     */
    private BigDecimal minLimit;
    /**
     * 计划数量
     */
    private BigDecimal number;
    /**
     * 付费方式:用英文逗号隔开
     */
    private String payMode;
    /**
     * 溢价百分比
     */
    private BigDecimal premiseRate;
    /**
     * 交易价格
     */
    private BigDecimal price;
    /**
     * 价格类型:0=固定的/1=变化的
     */
    private Integer priceType;
    /**
     * 计划剩余数量
     */
    private BigDecimal remainAmount;
    /**
     * 备注
     */
    private String remark;
    /**
     * 广告上下架状态:0=上架/1=下架/2=已关闭（删除）
     */
    private Integer status;
    /**
     * 付款期限
     */
    private Integer timeLimit;
    /**
     * 广告最后更新时间
     */
    private Date updateTime;
    /**
     *
     */
    private String username;
    /**
     *
     */
    private Long version;
    /**
     * 币种:引用Otc_Coin表
     */
    private Long coinId;
    /**
     * 国家:应用county表
     */
    private String country;
    /**
     * 广告拥有者
     */
    private Long memberId;
    /**
     * 需要交易方已绑定手机
     */
    private Integer needBindPhone;
    /**
     * 是否使用优惠支付,0使用，1不使用
     */
    private Integer needPutonDiscount;
    /**
     * 需要交易方已进行实名认证
     */
    private Integer needRealname;
    /**
     * 需要交易方至少完成过N笔交易（默认为0）
     */
    private Integer needTradeTimes;
    /**
     * 同时最大处理订单数 (0 = 不限制)
     */
    private Integer maxTradingOrders;
    /**
     * 广告置顶，值越大越前面
     */
    private Integer sort;
    /**
     * 广告在线时长
     */
    private Long totalOnlineMinites;
    /**
     * 最后一次上线时间
     */
    private Date lastOnlineTime;
    /**
     * 最后一次上线时间
     */
    private Date todayOnlineTime;
    /**
     * 广告在线时长
     */
    private Long todayTotalOnlineMinites;
}
