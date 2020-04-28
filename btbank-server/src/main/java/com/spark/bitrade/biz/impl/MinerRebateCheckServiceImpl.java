package com.spark.bitrade.biz.impl;

import com.spark.bitrade.api.dto.RebateRecordDTO;
import com.spark.bitrade.api.vo.RebateCheckLogVo;
import com.spark.bitrade.biz.MinerRebateCheckService;
import com.spark.bitrade.repository.service.BtBankRebateRecordService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.UnConfirmVo;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MinerRebateCheckServiceImpl
 *
 * @author biu
 * @since 2019/12/9 17:55
 */
@Service
public class MinerRebateCheckServiceImpl implements MinerRebateCheckService {

    private final Logger log = LoggerFactory.getLogger("CHECK");

    private MemberWalletService memberWalletService;
    private BtBankRebateRecordService rebateRecordService;

    @Async
    @Override
    public void checkRebate(Date begin) {

        // 获取未提交的记录
        MessageRespResult<List<UnConfirmVo>> result = memberWalletService.unConfirmRecords();
        if (!result.isSuccess()) {
            log.info("{}}", new RebateCheckLogVo("获取未提交记录失败", result));
            return;
        }

        List<UnConfirmVo> data = result.getData();
        if (data == null) {
            log.info("{}", new RebateCheckLogVo("暂无未提交的记录", result));
            return;
        }

        // 获取所有的奖励发放流水
        List<RebateRecordDTO> records = rebateRecordService.getRecordsCreatedAfter(begin);
        if (records.isEmpty()) {
            log.info("{}", new RebateCheckLogVo("不存在奖励发放流水", MessageRespResult.success()));
            return;
        }

        final Map<Long, RebateRecordDTO> map = records.stream().collect(Collectors.toMap(RebateRecordDTO::getId, (v) -> v));
        data.parallelStream().forEach(x -> {
            RebateRecordDTO dto = map.get(NumberUtils.toLong(x.getRefId(), 0L));
            if (dto != null && dto.getRebateMemberId().equals(x.getMemberId()) && x.getRefId().equals("" + dto.getId())) {
                try {
                    MessageRespResult<Boolean> resp = memberWalletService.doConfirm(x);
                    log.info("{}", new RebateCheckLogVo(dto.getRebateMemberId(), x.getId(), dto.getId(), "操作结果", resp));
                } catch (RuntimeException ex) {
                    log.info("{}", new RebateCheckLogVo(dto.getRebateMemberId(), x.getId(), dto.getId(), "出现异常", MessageRespResult.error(ex.getMessage())));
                }
            }
        });
    }

    @Autowired
    public void setMemberWalletService(MemberWalletService memberWalletService) {
        this.memberWalletService = memberWalletService;
    }

    @Autowired
    public void setRebateRecordService(BtBankRebateRecordService rebateRecordService) {
        this.rebateRecordService = rebateRecordService;
    }
}
