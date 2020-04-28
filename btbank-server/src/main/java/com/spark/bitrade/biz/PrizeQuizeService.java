package com.spark.bitrade.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.api.vo.MinerPrizeQuizeVo;
import com.spark.bitrade.api.vo.PrizeQuizeRecordVO;
import com.spark.bitrade.api.vo.PrizeQuizeVo;
import com.spark.bitrade.api.vo.QueryVo;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.MinerPrizeQuizeTransaction;
import com.spark.bitrade.repository.entity.PrizeQuizeRecord;
import com.spark.bitrade.util.MessageRespResult;

import java.util.Date;
import java.util.function.Function;

/**
 * @author qiuyuanjie
 * @time 2020.01.02.10:34
 */
public interface PrizeQuizeService{
    /**
     * 获取字典配置
     * @param key
     */
    public Object getConfig(String key);
    /**
     * 查询币种活动场次
     * @param coinUnit
     */
    public int getCntByCoinUnit(String coinUnit);
    /**
     * 查询该币种今天是否已经生成活动
     * @param coinUnit
     */
    public boolean existRecord(String coinUnit, Date startTime);

    /**
     * 查询开始时间的记录
     * @param coinUnit
     */
    public PrizeQuizeRecord queryRecordByStartTime(String coinUnit,Date startTime);
    /**
     * 更新总金额和参与人数信息
     * @param coinUnit
     */
    public boolean minerJoinQuiz(String coinUnit,Long prieQuizeId,MinerPrizeQuizeTransaction minerPrizeQuizeTransaction);
    /**
     * 获取当前活动
     * @param memberId 用户id
     * @return
     */
    MessageRespResult<PrizeQuizeVo> getCurrentPrize(Long memberId);

    /**
     * 获取当前活动
     * @return
     */
    PrizeQuizeRecord getPrize();

    /**
     * 获取竞猜往期记录的结果
     * @param queryVo 查询条件
     * @return
     */
    MessageRespResult<IPage<PrizeQuizeRecordVO>> getPrizeResult(QueryVo queryVo);
    /**
     * 查询今天开奖记录
     * @return
     */
    PrizeQuizeRecord getPrizeQuizeRecord();


    Boolean updateMinerResult(PrizeQuizeRecord prizeQuizeRecord);

    int queryTotalPrizeQuize(PrizeQuizeRecord prizeQuizeRecord);

    /**
     * 获得用户自己的竞猜记录
     * @param memberId 用户id
     * @param queryVo 查询条件
     * @return
     */
    IPage<MinerPrizeQuizeVo> minerPrizeRecord(Long memberId, QueryVo queryVo);
    /**
     * 失败账户归集
     * @param prizeQuizeRecord 活动记录
     * @return
     */
    Boolean collect(PrizeQuizeRecord prizeQuizeRecord);
    /**
     * 竞猜成功返还金额
     * @param prizeQuizeRecord 活动记录
     * @return
     */
    public Boolean release(PrizeQuizeRecord prizeQuizeRecord);
    /**
     * 参加活动
     * @param minerPrizeQuizeTransaction 参加活动
     * @return
     */
    Boolean joinPrizeQuize(MinerPrizeQuizeTransaction minerPrizeQuizeTransaction);
    /**
     * 获得竞猜配置
     * @param key
     * @return
     */
    public <T> T getConfig(String key, Function<Object, T> convert, T defaultValue);

    /**
     * 根据ID获取竞猜活动
     * @param activityId 活动ID
     * @return
     */
    PrizeQuizeRecord getPrizeActivityManage(Long activityId);

    /**
     * 判断用户是否参加了当期活动
     * @param record 当期活动
     * @param memberId 用户id
     * @return
     */
    boolean minerIsActivity(PrizeQuizeRecord record, Long memberId);

    /**
     * 获取后台配置的最小金额
     * @return 后台配置的最小金额
     */
    Long getMinAmount();

    /**
     * 获取后台配置的最大金额
     * @return
     */
    Long getMaxAmount();

    /**
     * 用户下注功能
     *
     * @param member
     * @param record 当期活动
     * @param transaction 当期交易
     * @return
     */
    Boolean minerBetting(Member member, PrizeQuizeRecord record, MinerPrizeQuizeTransaction transaction);

    /**
     * 检查活动是否可参与等 多种状态
     * @param record 活动
     * @param member 用户
     */
    void checkPrize(PrizeQuizeRecord record,Member member);



}
