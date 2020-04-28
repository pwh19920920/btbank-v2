package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.api.vo.*;
import com.spark.bitrade.biz.EnterpriseService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.EnterpriseMiner;
import com.spark.bitrade.repository.entity.EnterpriseMinerApplication;
import com.spark.bitrade.repository.entity.EnterpriseMinerTransaction;
import com.spark.bitrade.repository.service.EnterpriseMinerApplicationService;
import com.spark.bitrade.repository.service.EnterpriseMinerService;
import com.spark.bitrade.repository.service.EnterpriseMinerTransactionService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.StatusUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * EnterpriseServiceImpl
 *
 * @author biu
 * @since 2019/12/23 17:39
 */
@Slf4j
@Service
@AllArgsConstructor
public class EnterpriseServiceImpl implements EnterpriseService {

    private EnterpriseMinerService minerService;
    private EnterpriseMinerTransactionService transactionService;
    private MemberWalletService memberWalletService;
    private EnterpriseMinerApplicationService applicationService;

    @Override
    public Optional<EnterpriseMinerVO> findByMemberId(Long memberId) {
        EnterpriseMiner miner = minerService.findByMemberId(memberId);
        return Optional.ofNullable(miner).map(v -> {
            BigDecimal yesterday = transactionService.sumRewardOfYesterday(memberId);
            EnterpriseMinerVO vo = EnterpriseMinerVO.of(v);
            vo.setYesterday(yesterday);
            return vo;
        });
    }

    @Override
    public ApplicationResultVO apply(Long memberId, ApplicationVO vo) {

        vo.check();

        if (applicationService.hasApplication(memberId)) {
            // throw new BtBankException(4001, "存在审核中的申请");
            throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_APPLY_EXIST);
        }
        if (vo.getType() == null) {
            // throw new BtBankException(4001, "申请类型错误");
            throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_APPLY_TYPE_ERROR);
        }

        EnterpriseMiner miner = minerService.findByMemberId(memberId);

        if (vo.join() && miner != null) {
            // throw new BtBankException(4001, "已经是企业矿工");
            throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_IS_ALLREADY);
        }

        if (vo.quit() && miner == null) {

            //throw new BtBankException(4001, "已经不是企业矿工");
            throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_IS_NOTEXIST);
        }

        // 退出申请拷贝最后一条的记录信息
        if (vo.quit()) {
            // 查询配置，如果配置关闭直接放过，如果配置未关闭提示先划转企业矿池余额
            if (miner.getBalance().compareTo(new BigDecimal(0.001)) == 1) {
                // throw new BtBankException(4001, "企业矿池有可用余额，请先转出");
                //log.info("判断余额 {}",miner);
                throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_BALANCE_NOTZERO);

            }
            EnterpriseMinerApplication app = applicationService.latestJoinApplication(memberId);
            if (app != null) {
                Integer type = vo.getType();
                vo.copy(app);
                vo.setType(type);
            }
        }

        EnterpriseMinerApplication to = vo.to(memberId);
        to.setCreateTime(new Date());
        if (applicationService.save(to)) {
            return ApplicationResultVO.of(to);
        }

        throw new BtBankException(CommonMsgCode.FAILURE);
    }

    @Override
    public ApplicationResultVO findApplication(Long memberId) {
        EnterpriseMinerApplication application = applicationService.latestApplication(memberId);
        if (application == null) {
            //throw new BtBankException(4001, "未找到任何申请记录");
            throw new BtBankException(BtBankMsgCode.ALREADY_ATTEND_ACTIVITY);
        }
        ApplicationResultVO vo = ApplicationResultVO.of(application);

        if (StatusUtils.equals(1, application.getType()) && StatusUtils.equals(1, application.getStatus())) {
            vo.setResult(findByMemberId(memberId).orElse(null));
        }

        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean transfer(TransferVo vo) {
        EnterpriseMinerTransaction tx = null;
        TransactionType type = null;
        BigDecimal amount = BigDecimal.ZERO;
        // 转入
        if (vo.isIn()) {
            // 写入db
            tx = transactionService.preTransfer(vo.getMemberId(), vo.getAmount());
            type = TransactionType.ENTERPRISE_TRANSFER_OUT;
            amount = vo.getAmount().negate();
        }
        // 转出
        if (vo.isOut()) {
            // 写入db
            tx = transactionService.preTransfer(vo.getMemberId(), vo.getAmount().negate());
            type = TransactionType.ENTERPRISE_TRANSFER_IN;
            amount = vo.getAmount().abs();
        }

        if (tx == null) {
            throw new BtBankException(4001, "无效的参数");
        }

        // 划转
        WalletChangeRecord record = memberWalletService.tryTrade(type,
                vo.getMemberId(),
                "BT",
                "BT",
                amount,
                tx.getId(),
                type.getCnName());

        if (record == null) {
            log.error("划转失败 [ memberId = {}, txId = {}, amount = {} ",
                    vo.getMemberId(), tx.getId(), amount);
            throw new BtBankException(CommonMsgCode.FAILURE);
        }

        boolean cancel = false;
        try {
            // 加帐
            if (transactionService.confirmTransfer(tx, record.getId())) {
                boolean b = memberWalletService.confirmTrade(vo.getMemberId(), record.getId());
                if (!b) {
                    cancel = true;
                    throw new BtBankException(CommonMsgCode.FAILURE);
                }
            } else {
                cancel = true;
                throw new BtBankException(CommonMsgCode.FAILURE);
            }
        } catch (BtBankException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new BtBankException(CommonMsgCode.of(500, ex.getMessage()));
        } finally {
            if (cancel) {
                boolean b = memberWalletService.rollbackTrade(vo.getMemberId(), record.getId());
                log.info("执行转入回退 [ memberId = {}, txId = {}, amount = {}, result = {}",
                        vo.getMemberId(), tx.getId(), amount, b);
            }
        }
        return true;
    }

    @Override
    public EnterpriseMinerTransactionsVO query(Long memberId, List<Integer> types, QueryVo vo) {
        QueryWrapper<EnterpriseMinerTransaction> query = new QueryWrapper<>();
        query.eq("member_id", memberId).in("type", types);

        vo.fillRange(query, "create_time");
        query.orderByDesc("create_time");

        IPage<EnterpriseMinerTransaction> page = transactionService.page(vo.toPage(), query);

        EnterpriseMinerTransactionsVO ret = new EnterpriseMinerTransactionsVO();
        ret.setContent(page.getRecords());
        ret.setTotalElements(page.getTotal());
        return ret;
    }

    @Override
    public IPage<EnterpriseMinerTransaction> page(Long memberId, List<Integer> types, QueryVo vo) {
        QueryWrapper<EnterpriseMinerTransaction> query = new QueryWrapper<>();
        query.eq("member_id", memberId);

        if (types != null && types.size() > 0) {
            query.in("type", types);
        }

        vo.fillRange(query, "create_time");
        query.orderByDesc("create_time");

        return transactionService.page(vo.toPage(), query);
    }

    @Override
    public boolean isAvailableEnterpriseMiner(Long memberId) {
        QueryWrapper<EnterpriseMiner> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", memberId).eq("deleted", 0).eq("status", 1);
        return minerService.count(wrapper) > 0;
    }
}
