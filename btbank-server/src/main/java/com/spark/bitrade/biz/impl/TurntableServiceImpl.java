package com.spark.bitrade.biz.impl;

import com.spark.bitrade.api.dto.ActivitiesDTO;
import com.spark.bitrade.api.dto.ActivitiesPrizeDTO;
import com.spark.bitrade.api.vo.ActivitiesCarouselVO;
import com.spark.bitrade.api.vo.ActivitiesDrawVO;
import com.spark.bitrade.api.vo.ActivitiesVO;
import com.spark.bitrade.api.vo.ActivitiesWinningVO;
import com.spark.bitrade.biz.TurntableGiveOutService;
import com.spark.bitrade.biz.TurntableService;
import com.spark.bitrade.biz.support.TurntableGiveOutDispatcher;
import com.spark.bitrade.config.AliyunConfig;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.repository.entity.TurntableActivities;
import com.spark.bitrade.repository.entity.TurntablePrize;
import com.spark.bitrade.repository.entity.TurntableWinning;
import com.spark.bitrade.repository.service.*;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.util.AliyunUtil;
import com.spark.bitrade.util.StatusUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TurntableServiceImpl
 *
 * @author biu
 * @since 2020/1/8 10:08
 */
@Slf4j
@Service
@AllArgsConstructor
public class TurntableServiceImpl implements TurntableService {

    private BtBankConfigService configService;
    private TurntableInvolvementService involvementService;
    private TurntableWinningService winningService;
    private TurntableActivitiesService activitiesService;
    private TurntablePrizeService prizeService;
    private TurntableInvolvedRecordService recordService;
    private TurntableGiveOutService giveOutService;

    private TurntableGiveOutDispatcher giveOutDispatcher;

    private AliyunConfig aliyunConfig;

    @Override
    public boolean activityIsOpen() {
        return configService.getConfig(BtBankSystemConfig.TURNTABLE_ACTIVITY_SWITCH, (v) -> "1".equals(v.toString()), false);
    }

    @Override
    public ActivitiesVO getActivities() {
        TurntableActivities act = activitiesService.getInProgressOrLatestActivities();
        if (act == null) {
            throw BtBankMsgCode.TURNTABLE_ACTIVITY_NOT_FOUND.asException();
        }
        List<TurntablePrize> prizes = prizeService.getPrizes(act.getId());
        ActivitiesVO activities = ActivitiesVO.instanceOf(act, prizes);
        activities.peek(prize -> {
            try {
                // String privateUrl = AliyunUtil.getPrivateUrl(aliyunConfig, prize.getImage());
                // 替换为base64
                prize.setImageOss(image2b64(prize.getImage(), false));
            } catch (Exception ex) {
                prize.setImageOss("none");
            }
        });
        return activities;
    }

    @Override
    public List<ActivitiesCarouselVO> carousel(Integer actId) {
        return winningService.getWinnings(actId).stream()
                .map(ActivitiesCarouselVO::instanceOf)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ActivitiesDrawVO draw(Integer actId, Member member) {
        long memberId = member.getId();
        ActivitiesDTO act = activitiesService.findById(actId);

        if (!act.isStarted()) {
            throw BtBankMsgCode.TURNTABLE_ACTIVITY_NOT_STARTED.asException();
        }

        if (act.isStopped()) {
            throw BtBankMsgCode.TURNTABLE_ACTIVITY_STOPPED.asException();
        }

        // 扣除次数
        if (!involvementService.decrChances(memberId)) {
            throw BtBankMsgCode.TURNTABLE_CHANCE_NOT_ENOUGH.asException();
        }

        // 抽奖
        ActivitiesPrizeDTO prize = act.draw();

        // 非参与奖 达到每人中奖次数限制 -> 变为参与奖
        if (prize.isNotNone()) {
            if (prize.getToplimit() > 0 && recordService.overTopLimit(memberId, prize.getId(), prize.getToplimit())) {
                prize = ActivitiesPrizeDTO.noneOf(100);
            }
        }

        // 写入记录
        if (!recordService.record(actId, memberId, prize.getId())) {
            throw new MessageCodeException(CommonMsgCode.FAILURE);
        }

        // 扣除库存
        if (prize.isNotNone()) {
            // 扣除奖品库存
            if (!prizeService.decrement(prize.getId())) {
                prize = ActivitiesPrizeDTO.noneOf(100);
            }
        }

        Long winId = 0L;
        if (prize.isNotNone()) {

            TurntableWinning winning = winningService.winning(actId, member, prize);
            winId = winning.getId();

            // 异步
            if ("BT".equals(prize.getType())) {
                giveOutDispatcher.dispatch(winning);
            }
        } else {
            //谢谢参与修改为赠送8BT的奖励
            TurntableWinning winning = winningService.winning(actId, member, ActivitiesPrizeDTO.noneOfEight(100));
            winId = 0L;
            prize.setPriority(0);
            giveOutDispatcher.dispatch(winning);
        }

        return ActivitiesDrawVO.getInstance(actId, winId, prize.getPriority());
    }

    @Override
    public Integer getChances(Long memberId) {
        return involvementService.calculateChances(memberId);
    }

    @Override
    public boolean exchangePrize(Long id, Long memberId, String username, String mobile) {
        TurntableWinning win = winningService.getById(id);

        if (win == null || !win.getMemberId().equals(memberId)) {
            throw BtBankMsgCode.TURNTABLE_WINNING_NOT_FOUND.asException();
        }

        // 0：未发放 1：已发放 2：已完成
        if (StatusUtils.equals(1, win.getState())) {
            throw BtBankMsgCode.TURNTABLE_WINNING_GIVE_OUT.asException();
        }
        if (StatusUtils.equals(2, win.getState())) {
            throw BtBankMsgCode.TURNTABLE_WINNING_COMPLETED.asException();
        }

        return winningService.updateContact(id, username, mobile);
    }

    @Override
    public List<ActivitiesWinningVO> getWinnings(Integer actId, Long memberId) {
        return winningService.getWinnings(actId, memberId).stream()
                .map(ActivitiesWinningVO::instanceOf).peek(win -> {
                    try {
                        // String privateUrl = AliyunUtil.getPrivateUrl(aliyunConfig, win.getImage());
                        // 替换为base64
                        win.setImageOss(image2b64(win.getImage(), false));
                    } catch (Exception ex) {
                        win.setImageOss("none");
                    }
                }).collect(Collectors.toList());
    }

    @Override
    public boolean confirmReceived(Long winId, Long memberId) {
        TurntableWinning win = winningService.getById(winId);

        if (win == null || !win.getMemberId().equals(memberId)) {
            throw BtBankMsgCode.TURNTABLE_WINNING_NOT_FOUND.asException();
        }

        // 0：未发放 1：已发放 2：已完成
        if (StatusUtils.equals(0, win.getState())) {
            throw BtBankMsgCode.TURNTABLE_WINNING_NOT_GIVE_OUT.asException();
        }
        if (StatusUtils.equals(2, win.getState())) {
            throw BtBankMsgCode.TURNTABLE_WINNING_COMPLETED.asException();
        }

        return winningService.confirmReceived(winId);
    }

    @Override
    public void giveOut(Long winId) {
        TurntableWinning winning = winningService.getById(winId);
        if (winning == null || StatusUtils.equals(2, winning.getState())) {
            return;
        }

        giveOutService.giveOut(winning);
    }

    private String image2b64(String uri, boolean convertible) throws Exception {

        // 确定图片格式
        String prefix = "data:image/" + uri.substring(uri.lastIndexOf(".") + 1) + ";base64,";
        // oss 地址
        String privateUrl = AliyunUtil.getPrivateUrl(aliyunConfig, uri);

        if (convertible) {
            InputStream is = null;
            ByteArrayOutputStream data = null;
            try {
                // 创建URL
                URL url = new URL(privateUrl);
                byte[] by = new byte[1024];
                // 创建链接
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                is = conn.getInputStream();
                data = new ByteArrayOutputStream();
                // 将内容读取内存中
                int len;
                while ((len = is.read(by)) != -1) {
                    data.write(by, 0, len);
                }
                // 对字节数组Base64编码
                return prefix + Base64.getEncoder().encodeToString(data.toByteArray());
            } finally {
                if (is != null) {
                    is.close();
                }
                if (data != null) {
                    data.close();
                }
            }
        } else {
            return privateUrl;
        }
    }
}
