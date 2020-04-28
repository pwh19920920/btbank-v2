package com.spark.bitrade.controller;

import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Country;
import com.spark.bitrade.service.CountryService;
import com.spark.bitrade.sms.KafkaSMSProvider;
import com.spark.bitrade.system.TemplateHandler;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.HttpRequestUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/api/v2")
@Api(description = "短信发送控制器")
public class SmsController {

    @Resource
    private KafkaSMSProvider smsProvider;

    @Resource
    private CountryService countryService;

    /**
     * 发送短信的接口
     * @param phone 手机号码
     * @param code 编码
     * @param country 国籍
     */
    @PostMapping("/sendMessageToUser")
    public void sendMessage(@RequestParam("phone")String phone,
                            @RequestParam("code")String code,
                            @RequestParam("country")String country){
        Country countryEntity = countryService.findone(country);

        String content = TemplateHandler.getInstance().handler("otc_member.ftl", Collections.singletonMap("code", code));
        smsProvider.sendSms("", countryEntity.getAreaCode(), phone, content);
    }

}
