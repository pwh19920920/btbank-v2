package com.spark.bitrade.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.ForeignMemberBankinfo;
import com.spark.bitrade.repository.entity.ForeignOfflineExchange;
import com.spark.bitrade.repository.entity.ForeignOnlineExchange;

import java.math.BigDecimal;
import java.util.List;

public interface ForeignService {


    boolean refound(Member member, ForeignOfflineExchange foreignOfflineExchange);


    IPage<ForeignOfflineExchange> offlineorderlist(Member member, Integer current, Integer size);

    IPage<ForeignOnlineExchange> onlineorderlist(Member member, Integer current, Integer size);

    boolean onlineorder(Member member, Long bankId, BigDecimal buyCount, String exchangeSwapCurrency,String onineRate);

    boolean offlineorder(Member member, String address, Long addressId, BigDecimal buyCount, String exchangeSwapCurrency, String offineRate);


    boolean collectoffline(ForeignOfflineExchange exchange, Long account);
    boolean collectonline(ForeignOnlineExchange exchange, Long account);
    List<ForeignOfflineExchange> offlineCollectOrderList();
    List<ForeignOnlineExchange> onlineCollectOrderList();
    boolean handleDrawServiceFee( ForeignOfflineExchange exchange);
    List<ForeignOfflineExchange> offlinedrawServiceFeeList();
    List<ForeignOfflineExchange> offlineSystemRefoundList();

    boolean handleSystemRefound(ForeignOfflineExchange foreignOfflineExchange,Long account);

    boolean savebank(ForeignMemberBankinfo bankInfo);

    boolean updatebank(ForeignMemberBankinfo bankInfo);
}
