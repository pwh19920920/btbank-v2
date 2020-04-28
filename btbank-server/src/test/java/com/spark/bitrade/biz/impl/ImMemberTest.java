package com.spark.bitrade.biz.impl;

import com.alibaba.fastjson.JSON;

import com.spark.bitrade.biz.ImService;
import com.spark.bitrade.repository.entity.ImMember;
import com.spark.bitrade.repository.entity.MemberVo;
import com.spark.bitrade.repository.entity.SysUser;
import com.spark.bitrade.repository.service.ImMemberService;
import com.spark.bitrade.repository.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Slf4j
public class ImMemberTest {
    @Autowired
    private ImService imService;
    @Autowired
    private ImMemberService imMemberService;
    @Autowired
    private SysUserService sysUserService;
    @Test
    public void TestUnregisMember(){
        List<MemberVo> list = imService.getUnRegistImMiner();
        System.out.println(JSON.toJSONString(list));
    }

    @Test
    public void TestMember(){
        ImMember imMember = imService.getImMemberByMemberId(360482L);
        System.out.println(JSON.toJSONString(imMember));
    }
    @Test
    public void Testminner(){
        List<MemberVo> memberVolist = imService.getUnRegistImMiner();
        for(MemberVo memberVo: memberVolist){
            memberVo.setType("minner");
            ImMember imMember =   imService.syncImMember(memberVo);
            if(imMember!=null){
                imMember.setUserType(1);
                if(imMemberService.save(imMember)){
                    log.info("同步IM用户{}",imMember);
                }
            }else{
                log.error("同步IM用户失败{}",memberVo);
            }
        }
    }
    @Test
    public void TestsysUser(){
        List<ImMember> imMemberList = imMemberService.list();
        List<SysUser> sysuserLst = sysUserService.getUnSyncUser(imMemberList);
        for(SysUser sysUser: sysuserLst){
            MemberVo memberVo = new MemberVo();
            memberVo.setMemberId(sysUser.getId());
            memberVo.setEmail(sysUser.getEmail());
            memberVo.setRealName(sysUser.getRealName());
            memberVo.setType("sys");
            ImMember imMembersync =   imService.syncImMember(memberVo);
            if(imMembersync!=null){
                imMembersync.setUserType(2);
                if(imMemberService.save(imMembersync)){
                    log.info("同步IM系统用户{}",imMembersync);
                }
            }else{
                log.error("同步IM系统用户失败{}",memberVo);
            }
        }
    }
    @Test
    public void TestGetMember(){
        MemberVo memberVo = imService.findInfo(360606L);
        System.out.println(memberVo);
    }

    @Test
    public void TestGetkfMember(){
        List<MemberVo> members = imService.getUnRegistImKefuMiner();
        System.out.println(members);
    }


}
