package com.spark.bitrade.biz;

import com.spark.bitrade.repository.entity.PictureAccessory;

import java.util.List;

/**
 * 图片附件处理
 * * @author Administrator * @time 2019.12.01 18:23
 */
public interface PictureService {

    /**
     * otc商家挖矿付款信息凭证
     *
     * @param id                otc_order_id
     * @param paymentReceiptUrl 付款回执
     * @param transferUrl       转账信息
     */
    void saveMinerPayUrl(Long id, String paymentReceiptUrl, String transferUrl);

    /**
     * otc商家挖矿付款信息凭证
     *
     * @param id   otc_order_id
     * @param type 类型
     * @param url  凭证信息
     */
    void saveMinerPayUrl(Long id, Integer type, String url);

    /**
     * otc商家挖矿付款信息凭证查询
     *
     * @param id otc_order_id
     * @return
     */
    List<PictureAccessory> getMinerPayUrl(Long id);
}
