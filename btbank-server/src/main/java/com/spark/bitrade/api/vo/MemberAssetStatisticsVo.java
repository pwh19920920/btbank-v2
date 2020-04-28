package com.spark.bitrade.api.vo;

import lombok.Data;

/**
 * Created by mahao on 2019/12/25.
 */
@Data
public class MemberAssetStatisticsVo {
    /**
     * 用户ID
     */
    private Long memberId;

    /**
     * 手机号
     */
    private String mobilePhone;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 矿工级别
     */
    private String minerGrade;

    /**
     * 锁仓本金
     */
    private Double lockCoin;

    /**
     * 活期宝余额
     */
    private Double hqbBalance;

    /**
     * 矿池总额
     */
    private Double bttotalBalance;

    /**
     * 直接推荐矿工人数
     */
    private Integer invitePeople;

    /**
     * 挖矿当日收益
     */
    private Double dayRewardSum;

    /**
     * 挖矿累计收益
     */
    private Double gotRewardSum;

    /**
     * 理财账户当日释放
     */
    private Double unlockCoinToday;


    /*public MemberAssetStatisticsVo (MemberAssetStatistics memberAssetStatistics){
        if(memberAssetStatistics.getDayRewardSum()!=null){
            this.dayRewardSum = memberAssetStatistics.getDayRewardSum().doubleValue();
        }
        if( memberAssetStatistics.getGotRewardSum()!=null){
            this.gotRewardSum = memberAssetStatistics.getGotRewardSum().doubleValue();
        }
        if(memberAssetStatistics.getBttotalBalance()!=null){
            this.bttotalBalance = memberAssetStatistics.getBttotalBalance().doubleValue();
        }
        if(memberAssetStatistics.getLockCoin()!=null){
            this.lockCoin = memberAssetStatistics.getLockCoin().doubleValue();
        }
        if(memberAssetStatistics.getHqbBalance()!=null){
            this.hqbBalance = memberAssetStatistics.getHqbBalance().doubleValue();
        }
        if(memberAssetStatistics.getUnlockCoinToday()!=null){
            this.unlockCoinToday = memberAssetStatistics.getUnlockCoinToday().doubleValue();
        }
        this.memberId = memberAssetStatistics.getMemberId();
        this.realName = memberAssetStatistics.getRealName();
        this.invitePeople = memberAssetStatistics.getInvitePeople();
        this.mobilePhone = memberAssetStatistics.getMobilePhone();
        this.minerGrade = memberAssetStatistics.getMinerGrade();
    }*/

    public MemberAssetStatisticsVo() {

    }
}
