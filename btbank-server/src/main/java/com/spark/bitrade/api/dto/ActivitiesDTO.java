package com.spark.bitrade.api.dto;

import com.spark.bitrade.repository.entity.TurntablePrize;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
 * ActivitiesDTO
 *
 * @author biu
 * @since 2020/1/8 16:02
 */
@Data
public class ActivitiesDTO implements Serializable {

    private Integer id;
    private Long start;
    private Long end;

    private List<ActivitiesPrizeDTO> prizes;

    public boolean isStarted() {
        long now = Calendar.getInstance().getTimeInMillis();
        return now > start;
    }

    public boolean isStopped() {
        long now = Calendar.getInstance().getTimeInMillis();
        return now > end;
    }

    public void setPrizes(List<TurntablePrize> prizes) {
        // 按优先级排序
        prizes.sort(Comparator.comparingInt(TurntablePrize::getPriority));

        double rate = 0d;
        for (TurntablePrize prize : prizes) {
            rate += prize.getRate();
            addPrize(ActivitiesPrizeDTO.instanceOf(prize));
        }
        // 参与奖
        addPrize(ActivitiesPrizeDTO.noneOf(100d - rate));
    }

    private void addPrize(ActivitiesPrizeDTO dto) {
        if (prizes == null) {
            prizes = new ArrayList<>();
        }
        prizes.add(dto);
    }

    public ActivitiesPrizeDTO draw() {
        if (prizes == null || prizes.isEmpty()) {
            throw new IllegalArgumentException("未设置奖品");
        }

        // 参与奖的概率
        double none = 0d;
        List<Double> rates = new ArrayList<>();
        for (ActivitiesPrizeDTO prize : prizes) {
            boolean isNone = "NONE".equals(prize.getType());
            Double rate = prize.getRate();
            if (rate == null) {
                rate = 0d;
            }
            // 没有库存的时候
            if (!isNone && 0 == prize.getStock()) {
                rate = 0d;
                // 中奖概率给参与奖
                none += prize.getRate();
            }
            // 参与奖
            if (isNone) {
                rates.add((none + rate) / 100);
            } else {
                rates.add(rate / 100);
            }
        }

        return prizes.get(lottery(rates));
    }

    // 抽奖函数
    private int lottery(List<Double> rates) {
        int size = rates.size();

        // 计算总概率
        double sum = 0d;
        for (double rate : rates)
            sum += rate;

        // 计算每个物品在总概率的基础下的概率情况
        List<Double> sortable = new ArrayList<Double>(size);
        double temp = 0d;
        for (double rate : rates) {
            temp += rate;
            sortable.add(temp / sum);
        }

        // 产生一个随机数
        // 理论产生随机数的概率是均等的，那么相应区间包含数的多少体现了物品的中奖率
        // 将随机放入计算后概率集合排序，得到随机数所在区块的索引即表示中奖该区块的物品
        double random = Math.random();
        sortable.add(random);
        Collections.sort(sortable);
        return sortable.indexOf(random);
    }
}
