package com.spark.bitrade.biz;

import com.spark.bitrade.repository.entity.TurntableWinning;
import com.spark.bitrade.repository.entity.TurntableWinningTransaction;

/**
 * TurntableGiveOutService
 *
 * @author biu
 * @since 2020/1/9 9:25
 */
public interface TurntableGiveOutService {

    void async(TurntableWinning winning);

    void async(TurntableWinningTransaction tx);

    void handle(TurntableWinning winning);

    void confirm(TurntableWinningTransaction tx);

    void giveOut(TurntableWinning winning);
}
