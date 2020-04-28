package com.spark.bitrade.api.vo;

import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.EnterpriseMinerApplication;
import com.spark.bitrade.util.IdcardValidator;
import com.spark.bitrade.util.StatusUtils;
import com.spark.bitrade.util.ValidateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * ApplicationVO
 *
 * @author biu
 * @since 2019/12/24 16:17
 */
@Data
@ApiModel("企业矿工申请")
public class ApplicationVO {

    /**
     * 申请类型 1：加入申请 2：退出申请
     */
    @ApiModelProperty(value = "申请类型 1：加入申请 2：退出申请", example = "")
    private Integer type;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", example = "")
    private String realName;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码", example = "")
    private String mobilePhone;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱", example = "")
    private String email;

    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号", example = "")
    private String idCard;

    /**
     * 身份证正面
     */
    @ApiModelProperty(value = "身份证正面", example = "")
    private String idCardFront;

    /**
     * 身份证背面
     */
    @ApiModelProperty(value = "身份证背面", example = "")
    private String idCardBack;

    /**
     * 手持身份证
     */
    @ApiModelProperty(value = "手持身份证", example = "")
    private String idCardInHand;

    /**
     * 营业执照
     */
    @ApiModelProperty(value = "营业执照", example = "")
    private String businessLicense;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述，退出申请时填写", example = "")
    private String description;

    public void check() {
        // 加入
        if (StatusUtils.equals(1, type)) {
            if (com.aliyuncs.utils.StringUtils.isEmpty(realName) || !ValidateUtil.isChineseName(realName)) {
                throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_NEMAE_CHINESE);
            }

            if (com.aliyuncs.utils.StringUtils.isEmpty(mobilePhone) || !ValidateUtil.isMobilePhone(mobilePhone)) {
                throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_MOBILE_PHONE);
            }

            if (com.aliyuncs.utils.StringUtils.isEmpty(idCard) || !IdcardValidator.isValidate18Idcard(idCard)) {
                throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_ID_CARD);
            }

            if (com.aliyuncs.utils.StringUtils.isEmpty(idCardFront)) {
                throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_ID_CARD_FRONT);
            }
            if (com.aliyuncs.utils.StringUtils.isEmpty(idCardBack)) {
                throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_ID_CARD_BACK);
            }
            if (com.aliyuncs.utils.StringUtils.isEmpty(idCardInHand)) {
                throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_ID_CARD_HAND);
            }
            if (com.aliyuncs.utils.StringUtils.isEmpty(businessLicense)) {
                throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_BUSINESS_LICENSE);
            }
            if (!com.aliyuncs.utils.StringUtils.isEmpty(email) && !ValidateUtil.isEmail(email)) {
                throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_EMAIL);
            }
            return;
        }
        // 退出
        if (StatusUtils.equals(2, type)) {
            if (!StringUtils.hasText(description)) {
                throw new BtBankException(CommonMsgCode.BAD_REQUEST);
            }
            return;
        }

        throw new BtBankException(CommonMsgCode.BAD_REQUEST);
    }

    public boolean join() {
        return StatusUtils.equals(1, type);
    }

    public boolean quit() {
        return StatusUtils.equals(2, type);
    }

    public ApplicationVO copy(EnterpriseMinerApplication app) {

        this.type = app.getType();
        this.realName = app.getRealName();
        this.mobilePhone = app.getMobilePhone();
        this.email = app.getEmail();
        this.idCard = app.getIdCard();
        this.idCardFront = app.getIdCardFront();
        this.idCardBack = app.getIdCardBack();
        this.idCardInHand = app.getIdCardInHand();
        this.businessLicense = app.getBusinessLicense();
        return this;
    }

    public EnterpriseMinerApplication to(Long memberId) {
        EnterpriseMinerApplication app = new EnterpriseMinerApplication();

        app.setMemberId(memberId);

        app.setType(type);
        app.setRealName(realName);
        app.setMobilePhone(mobilePhone);
        app.setEmail(email);
        app.setIdCard(idCard);

        // oss 处理
        app.setIdCardFront(resolveImageUrl(idCardFront));
        app.setIdCardBack(resolveImageUrl(idCardBack));
        app.setIdCardInHand(resolveImageUrl(idCardInHand));
        app.setBusinessLicense(resolveImageUrl(businessLicense));

        app.setDescription(description);

        app.setStatus(0);

        return app;
    }

    private String resolveImageUrl(String url) {
        if (url == null || !url.contains("oss-")) {
            return url;
        }
        return url.split("[?]")[0].split("[|/]", 4)[3];
    }
}
