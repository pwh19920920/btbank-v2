package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.constants.UcMsgCode;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.mapper.MemberMapper;
import com.spark.bitrade.mapper.MemberSecuritySetMapper;
import com.spark.bitrade.service.MemberService;
import com.spark.bitrade.service.SlpMemberPromotionService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.GeneratorUtil;
import com.spark.bitrade.util.IdWorkByTwitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * (Member)表服务实现类
 *
 * @author archx
 * @since 2019-06-11 17:28:17
 */
@Slf4j
@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Resource
    private IdWorkByTwitter idWorkByTwitter;
    @Resource
    private SlpMemberPromotionService slpMemberPromotionService;
    @Resource
    private MemberSecuritySetMapper memberSecuritySetMapper;
    private static final String userNameFormat = "S%06d";

    @Override
    public Member login(String username, String password) {
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile_phone", username).or().eq("email", username);
        Member member = getOne(wrapper);
        AssertUtil.notNull(member, UcMsgCode.LOGIN_FALSE);

        String userPassWord = this.simpleHashPassword(password, member.getSalt());
        AssertUtil.isTrue(userPassWord.equals(member.getPassword()), UcMsgCode.LOGIN_FALSE);
        AssertUtil.isTrue(member.getStatus() != CommonStatus.ILLEGAL, UcMsgCode.ACCOUNT_DISABLE);
        return member;
    }

    @Override
    public boolean emailIsExist(String email) {
        return count(new QueryWrapper<Member>().eq("email", email)) > 0;
    }

    @Override
    public boolean phoneIsExist(String phone) {
        return count(new QueryWrapper<Member>().eq("mobile_phone", phone)) > 0;
    }

    @Override
    public boolean usernameIsExist(String username) {
        return count(new QueryWrapper<Member>().eq("username", username)) > 0;
    }

    @Override
    public Member loginWithUserId(Long userId, String username) {
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        //wrapper.eq("username", username).eq("id", userId);
        wrapper.eq("id", userId);
        return getOne(wrapper);
    }

    @Override
    public Member findByPhoneOrEmail(int mode, String account) {
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        if (mode == 0) {
            wrapper.eq("mobile_phone", account);
        } else if (mode == 1) {
            wrapper.eq("email", account);
        } else {
            return null;
        }
        return getOne(wrapper);
    }

    @Override
    public Member findByPromotion(String promotion) {
        return getOne(new QueryWrapper<Member>().eq("promotion_code", promotion));
    }

    @Override
    @CacheEvict(cacheNames = "member", key = "'entity:member:uid-'+#member.id")
    public boolean resetPassword(Member member, String password) {
        String newPassword = new SimpleHash("md5", password, member.getSalt(), 2).toHex().toLowerCase();
        Member reset = new Member();
        reset.setId(member.getId());
        reset.setStatus(member.getStatus());
        reset.setPassword(newPassword);
        return updateById(reset);
    }

    @Override
    public boolean phoneOrEmailIsExist(String username, String phone, String email) {
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        if (StringUtils.isNotBlank(phone)) {
            wrapper.or().eq("mobile_phone", phone);
        } else if (StringUtils.isNotBlank(email)) {
            wrapper.or().eq("email", email);
        }
        return count(wrapper) > 0;
    }

    @Override
    public boolean checkPromotion(String promotion) {
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        wrapper.eq("promotion_code", promotion);
        return count(wrapper) > 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Member register(LoginByPhone loginByPhone, LoginByEmail loginByEmail, String registerIP) {
        String loginNo = String.valueOf(idWorkByTwitter.nextId());
        String promotion = loginByPhone != null ? loginByPhone.getPromotion() : loginByEmail.getPromotion();
        String password = loginByPhone != null ? loginByPhone.getPassword() : loginByEmail.getPassword();
        // 盐
        String credentialsSalt = ByteSource.Util.bytes(loginNo).toHex().toLowerCase();
        // 新的生成密码规则
        String md5Password = simpleHashPassword(password, credentialsSalt);
        Member member = new Member();
        member.setMemberLevel(MemberLevelEnum.GENERAL);
        if (loginByPhone != null) {
            member.setCountry(loginByPhone.getCountry());
            member.setLocal(loginByPhone.getCountry());
            member.setUsername(StringUtils.isNotBlank(loginByPhone.getUsername()) ? loginByPhone.getUsername() : loginByPhone.getPhone());
            member.setMobilePhone(loginByPhone.getPhone());
        } else {
            member.setUsername(StringUtils.isNotBlank(loginByEmail.getUsername()) ? loginByEmail.getUsername() : loginByEmail.getEmail());
            member.setEmail(loginByEmail.getEmail());
        }
        member.setPassword(md5Password);
        member.setSalt(credentialsSalt);
        member.setIp(registerIP);
        member.setGoogleState(BooleanEnum.IS_FALSE);
        member.setRegistrationTime(new Date());
        member.setStatus(CommonStatus.NORMAL);
        member.setTransactions(0);
        member.setAppealTimes(0);
        member.setAppealSuccessTimes(0);
        member.setFirstLevel(0);
        member.setSecondLevel(0);
        member.setThirdLevel(0);
        member.setRealNameStatus(RealNameStatus.NOT_CERTIFIED);
        member.setLoginCount(0);
        member.setCertifiedBusinessStatus(CertifiedBusinessStatus.NOT_CERTIFIED);
        member.setPublishAdvertise(BooleanEnum.IS_TRUE);
        member.setTransactionStatus(BooleanEnum.IS_TRUE);

        // 保存用户信息
        boolean saveResult = save(member);
        AssertUtil.isTrue(saveResult, UcMsgCode.REGISTRATION_FAILED);

        // 更新推荐码(必须在保存后进行更新,否则ID无法获取)
        Member renew = new Member();
        renew.setId(member.getId());
        renew.setPromotionCode(String.format(userNameFormat, member.getId()) + GeneratorUtil.getNonceString(2));
        boolean renewResult = updateById(renew);
        AssertUtil.isTrue(renewResult, UcMsgCode.REGISTRATION_FAILED);

        // 保存推荐关系记录
        if (StringUtils.isNotBlank(promotion)) {
            // 获取推荐人
            Member memberPromotion = getOne(new QueryWrapper<Member>().eq("promotion_code", promotion));
            // 检测是否存在循环推荐关系
            AssertUtil.isTrue(slpMemberPromotionService.queryRecipt(memberPromotion.getId(), member.getId()), UcMsgCode.PROMOTION_BIND_CYCLE);

            // 设置推荐关系
            SlpMemberPromotion slpMemberPromotion = new SlpMemberPromotion();
            slpMemberPromotion.setMemberId(member.getId());
            slpMemberPromotion.setInviterId(memberPromotion.getId());
            slpMemberPromotion.setCreateTime(new Date());
            boolean savePromotion = slpMemberPromotionService.saveOrUpdate(slpMemberPromotion);
            AssertUtil.isTrue(savePromotion, UcMsgCode.REGISTRATION_FAILED);

            // 处理上级推荐人(不存在上级推荐人推荐关系,需要插入一条记录)
            SlpMemberPromotion upSlpMemberPromotion = slpMemberPromotionService.getById(memberPromotion.getId());
            if (upSlpMemberPromotion == null) {
                upSlpMemberPromotion = new SlpMemberPromotion();
                upSlpMemberPromotion.setMemberId(memberPromotion.getId());
                upSlpMemberPromotion.setInviterId(0L);
                upSlpMemberPromotion.setCreateTime(new Date());
                slpMemberPromotionService.saveOrUpdate(upSlpMemberPromotion);
            }
        }

        //手机注册默认开启手机登录、提币验证
        MemberSecuritySet memberSecuritySet = new MemberSecuritySet();
        memberSecuritySet.setMemberId(member.getId());
        if (loginByPhone != null) {
            memberSecuritySet.setIsOpenPhoneLogin(BooleanEnum.IS_TRUE);
            memberSecuritySet.setIsOpenPhoneUpCoin(BooleanEnum.IS_TRUE);
        } else {
            memberSecuritySet.setIsOpenPhoneLogin(BooleanEnum.IS_FALSE);
            memberSecuritySet.setIsOpenPhoneUpCoin(BooleanEnum.IS_FALSE);
        }
        AssertUtil.isTrue(memberSecuritySetMapper.insert(memberSecuritySet) > 0, UcMsgCode.REGISTRATION_FAILED);
        return member;
    }

    @Async
    @Override
    public void updateInviterTotal(Long memberId) {
        slpMemberPromotionService.updateTotal(memberId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Member bindPromotion(String phone, String email, String password, String promotionCode) {
        Member member = null;
        if (StringUtils.isNotBlank(phone)) {
            member = findByPhoneOrEmail(0, phone);
        } else if (StringUtils.isNotBlank(email)) {
            member = findByPhoneOrEmail(1, email);
        }
        AssertUtil.notNull(member, UcMsgCode.MEMBER_NOT_EXISTS);

        String userPassWord = this.simpleHashPassword(password, member.getSalt());
        AssertUtil.isTrue(userPassWord.equals(member.getPassword()), UcMsgCode.BIND_PROMOTION_PASSWORD_ERROR);

        // 校验是否存在绑定关系
        SlpMemberPromotion slpMemberPromotion = slpMemberPromotionService.getById(member.getId());
        AssertUtil.isTrue(slpMemberPromotion == null || slpMemberPromotion.getInviterId() <= 0, UcMsgCode.BIND_PROMOTION_EXISTS);

        // 绑定推荐关系
        Member promotionMember = findByPromotion(promotionCode);
        AssertUtil.notNull(promotionMember, UcMsgCode.PROMOTION_CODE_ERRO);

        // 检测是否存在循环推荐关系
        AssertUtil.isTrue(slpMemberPromotionService.queryRecipt(promotionMember.getId(), member.getId()), UcMsgCode.PROMOTION_BIND_CYCLE);

        // 记录绑定关系
        SlpMemberPromotion newSlpMemberPromotion = new SlpMemberPromotion();
        newSlpMemberPromotion.setInviterId(promotionMember.getId());
        newSlpMemberPromotion.setMemberId(member.getId());
        newSlpMemberPromotion.setCreateTime(new Date());
        boolean ret = slpMemberPromotionService.saveOrUpdate(newSlpMemberPromotion);
        AssertUtil.isTrue(ret, UcMsgCode.BIND_PROMOTION_FAIL);

        // 处理上级推荐人(不存在上级推荐人推荐关系,需要插入一条记录)
        SlpMemberPromotion upSlpMemberPromotion = slpMemberPromotionService.getById(promotionMember.getId());
        if (upSlpMemberPromotion == null) {
            upSlpMemberPromotion = new SlpMemberPromotion();
            upSlpMemberPromotion.setMemberId(promotionMember.getId());
            upSlpMemberPromotion.setInviterId(0L);
            upSlpMemberPromotion.setCreateTime(new Date());
            ret = slpMemberPromotionService.saveOrUpdate(upSlpMemberPromotion);
            AssertUtil.isTrue(ret, UcMsgCode.BIND_PROMOTION_FAIL);
        }

        return member;
    }

    /**
     * 处理会员的密码
     *
     * @param inputPassword 会员输入的密码
     * @param salt          盐
     * @return
     */
    @Override
    public String simpleHashPassword(String inputPassword, String salt) {
        AssertUtil.notNull(inputPassword, UcMsgCode.MISSING_PASSWORD);
        return new SimpleHash("md5", inputPassword, salt, 2).toHex().toLowerCase();
    }

    /**
     * 密码确认
     *
     * @param storagePassword 存储的密码
     * @param inputPassword   会员输入的密码
     * @param salt            盐
     * @return true=一样/false=不一样
     */
    @Override
    public boolean confirmPassword(String storagePassword, String inputPassword, String salt) {
        AssertUtil.notNull(storagePassword, UcMsgCode.MISSING_PASSWORD);
        if (storagePassword.equals(this.simpleHashPassword(inputPassword, salt))) {
            return true;
        }
        return false;
    }

    /**
     * 批量查询用户信息
     *
     * @param memberIds
     * @return true
     * @author shenzucai
     * @time 2019.07.04 9:10
     */
    @Override
    public List<Member> listMembersByIds(List<Long> memberIds) {
        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<Member>()
                .in("id", memberIds);

        return baseMapper.selectList(memberQueryWrapper);
    }

    /**
     * 获取会员详情
     *
     * @param id 会员ID
     * @return 会员详情
     */
    @Override
    public Member getMemberById(Long id) {
        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<Member>()
                .eq("id", id);
        return getBaseMapper().selectOne(memberQueryWrapper);
    }
}