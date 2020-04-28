package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.biz.PictureService;
import com.spark.bitrade.config.AliyunConfig;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.PictureAccessory;
import com.spark.bitrade.repository.service.PictureAccessoryService;
import com.spark.bitrade.util.AliyunUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * * @author Administrator * @time 2019.12.01 18:24
 */
@Slf4j
@Service
public class PictureServiceImpl implements PictureService {

    @Autowired
    private PictureAccessoryService pictureAccessoryService;

    @Autowired
    private AliyunConfig aliyunConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMinerPayUrl(Long id, String paymentReceiptUrl, String transferUrl) {

        PictureAccessory paymentReceipt = new PictureAccessory();
        paymentReceipt.setUrlPath(paymentReceiptUrl.split("[?]")[0].split("[|/]", 4)[3]);
        paymentReceipt.setRefId(id.toString());
        paymentReceipt.setRemark("otc商家挖矿orderSn");
        paymentReceipt.setType(0);

        pictureAccessoryService.save(paymentReceipt);

        PictureAccessory transferPicture = new PictureAccessory();
        transferPicture.setUrlPath(transferUrl.split("[?]")[0].split("[|/]", 4)[3]);
        transferPicture.setRefId(id.toString());
        transferPicture.setRemark("otc商家挖矿orderSn");
        transferPicture.setType(1);

        pictureAccessoryService.save(transferPicture);

    }

    @Override
    public void saveMinerPayUrl(Long id, Integer type, String url) {
        QueryWrapper<PictureAccessory> query = new QueryWrapper<>();
        query.eq("ref_id", id + "").eq("type", type);

        if (pictureAccessoryService.count(query) > 0) {
            pictureAccessoryService.lambdaUpdate()
                    .set(PictureAccessory::getUrlPath, url.split("[?]")[0].split("[|/]", 4)[3])
                    .eq(PictureAccessory::getRefId, id + "")
                    .eq(PictureAccessory::getType, type).update();
        } else {
            PictureAccessory pic = new PictureAccessory();
            pic.setUrlPath(url.split("[?]")[0].split("[|/]", 4)[3]);
            pic.setRefId(id.toString());
            pic.setRemark("otc商家挖矿orderSn");
            pic.setType(type);

            pictureAccessoryService.save(pic);
        }
    }

    @Override
    public List<PictureAccessory> getMinerPayUrl(Long id) {
        List<PictureAccessory> list = pictureAccessoryService.lambdaQuery().eq(PictureAccessory::getRefId, id + "").list();
        list.forEach(pictureAccessory -> {
                    try {
                        String uri = AliyunUtil.getPrivateUrl(aliyunConfig, pictureAccessory.getUrlPath());
                        pictureAccessory.setUrlPath(uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("付款凭证获取失败", e);
                        throw new BtBankException(CommonMsgCode.FAILURE);
                    }
                }
        );
        return list;
    }
}
