package com.spark.bitrade.facea.customizer;

import com.spark.bitrade.biz.OtcConfigService;
import com.spark.bitrade.constant.OtcConfigType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.facea.AuthenticationDto;
import com.spark.bitrade.facea.api.ApiResponse;
import com.spark.bitrade.facea.api.Authentication;
import com.spark.bitrade.facea.api.FaceRealm;
import com.spark.bitrade.jwt.HttpJwtToken;
import com.spark.bitrade.jwt.MemberClaim;
import com.spark.bitrade.service.MemberAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Slf4j
@Component
public class FaceRealmCustomizer implements FaceRealm {

    private final MongoTemplate mongoTemplate;
    private final OtcConfigService configService;

    private MemberAccountService memberAccountService;

    public FaceRealmCustomizer(MongoTemplate mongoTemplate, OtcConfigService configService) {
        this.mongoTemplate = mongoTemplate;
        this.configService = configService;
    }

    @Autowired
    @Lazy
    public void setMemberAccountService(MemberAccountService memberAccountService) {
        this.memberAccountService = memberAccountService;
    }

    @Override

    public boolean available() {
        // 人脸识别开关，可从数据库读取配置
        String value = configService.getValue(OtcConfigType.OTC_FACE_AUTHENTICATION_SWITCH);
        return "ON".equalsIgnoreCase(value);
    }

    @Override
    public Authentication doAuthentication(String token) {
        log.info("Realm -> @FaceAuthentication(value='xxx') -> {}", token);
        // 根据身份令牌获取 身份认证信息

        MemberClaim claim = HttpJwtToken.getInstance().verifyToken(token);
        if (claim != null) {
            Member member = memberAccountService.findMemberByMemberId(claim.userId);
            if (member != null) {
                String idNumber = member.getIdNumber();
                String realName = member.getRealName();

                Authentication authentication = new Authentication();
                authentication.setId(member.getId().toString());
                authentication.setIdCard(idNumber);
                authentication.setName(realName);

                // 为空时表示未认证过，当认证通过时将调用 onPassed
                // authentication.setB64image("存储的面部照片");
                authentication.setB64image(getBase64Image(member.getId(), idNumber));

                return authentication;
            }

        }
        // 未知用户
        throw new MessageCodeException(CommonMsgCode.UNKNOWN_ACCOUNT);
    }

    @Override
    public Exception response2Exception(Authentication authentication, ApiResponse response) {
        // 认证失败后将调用，产生自定义异常信息抛出
        log.error("身份认证失败 name = {}, code = {}, err = '{}'", authentication.getName(), response.getCode(), response.getMessage());
        return new MessageCodeException(CommonMsgCode.of(response.getCode(), response.getStatus() + " " + response.getMessage()));
    }

    @Override
    public void onPassed(Authentication authentication, byte[] image) {
        // 身份认证通过，可以将 image 更新存储
        log.info("认证通过 name = {}", authentication.getName());
        saveBase64Image(authentication, image);
    }

    private String getBase64Image(Long uid, String idNumber) {
        String _id = idNumber + ":" + uid;
        AuthenticationDto dto = mongoTemplate.findById(_id, AuthenticationDto.class, "face_authentication");
        if (dto != null) {
            return dto.getImage64();
        }
        return null;
    }

    private void saveBase64Image(Authentication authentication, byte[] image) {
        String _id = authentication.getIdCard() + ":" + authentication.getId();
        AuthenticationDto dto = mongoTemplate.findById(_id, AuthenticationDto.class, "face_authentication");

        if (dto == null) {
            dto = new AuthenticationDto();
            dto.setId(_id);
            dto.setUid(authentication.getId());
            dto.setIdNumber(authentication.getIdCard());
            dto.setRealName(authentication.getName());
            dto.setImage64(Base64.getEncoder().encodeToString(image));
            mongoTemplate.save(dto, "face_authentication");
            log.info("认证通过，更新用户信息 {}", dto);
        }
    }
}
