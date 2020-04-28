package com.spark.bitrade.api.controller;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.utils.StringUtils;
import com.spark.bitrade.api.vo.ImResult;
import com.spark.bitrade.biz.ImService;

import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.*;
import com.spark.bitrade.repository.service.*;
import com.spark.bitrade.util.ImUtils;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(tags = {"IM系统数据同步"})
@RequestMapping(path = "inner/sheduleImController", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class SheduleImController {

    private ImChatRoomService imChatRoomService;
    private ImService imService;
    private ImMemberService imMemberService;
    private SysUserService sysUserService;
    private ImGroupService imGroupService;
    private ImGroupMemberService imGroupMemberService;
    @ApiOperation(value = "矿工用户同步")
    @PostMapping("auto/registermember")
    public MessageRespResult registermember() {
        List<MemberVo> memberVolist = imService.getUnRegistImMiner();
        for(MemberVo memberVo: memberVolist){
            memberVo.setType("miner");
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
        return MessageRespResult.success();
    }
    @ApiOperation(value = "客服用户同步")
    @PostMapping("auto/registerkefumember")
    public MessageRespResult registerkefumember() {
        List<MemberVo> memberVolist = imService.getUnRegistImKefuMiner();
        for(MemberVo memberVo: memberVolist){
            memberVo.setType("kf");
            ImMember imMember =   imService.syncImMember(memberVo);
            if(imMember!=null){
                imMember.setUserType(2);
                if(imMemberService.save(imMember)){
                    log.info("同步IM用户{}",imMember);
                }
            }else{
                log.error("同步IM用户失败{}",memberVo);
            }
        }
        return MessageRespResult.success();
    }
    @ApiOperation(value = "系统用户同步")
    @PostMapping("auto/registersysuser")
    public MessageRespResult registersysuser() {
        List<ImMember> imMemberList = imMemberService.list();

        List<SysUser> sysuserLst = null;
        if(imMemberList.size()>0){
            sysuserLst = sysUserService.getUnSyncUser(imMemberList);
        }else{
            sysuserLst = sysUserService.list();
        }

        for(SysUser sysUser: sysuserLst){
            ImMember imMember = imMemberService.getImMemberByMemberId(sysUser.getId());
            if(imMember==null){
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
            }else{
                log.info("该系统用户已经同步");
            }

        }
        return MessageRespResult.success();
    }
    @ApiOperation(value = "创建群")
    @PostMapping("auto/registergroup")
    public MessageRespResult registergroup(String group) {
        ImGroup imGroup = JSON.parseObject(group,ImGroup.class);
        if(StringUtils.isEmpty(imGroup.getTname())){
            throw new BtBankException(4001, "请输入群名称");
        }
        if(StringUtils.isEmpty(imGroup.getIntro())){
            throw new BtBankException(4001, "请输入群介绍");
        }
        if(StringUtils.isEmpty(imGroup.getOwnerAcid())){
            throw new BtBankException(4001, "请输入群主acid");
        }
        ImMember imMember = imService.getImMemberByAcid(imGroup.getOwnerAcid());
        if(imMember==null){
            throw new BtBankException(4001, "群主数据未同步到IM系统");
        }
        imGroup.setGroupSize(1);
        ImResult imResult = imService.createGroup(imGroup);
        if(imResult!=null){
            //保存群tid
            imGroup.setTid(imResult.getTid());
            if(imGroupService.updateById(imGroup)){
                //添加群和用户的对应关系
                ImGroupMember imGroupMember = new ImGroupMember();
                imGroupMember.setAcid(imGroup.getOwnerAcid());
                imGroupMember.setTid(imGroup.getTid());
                imGroupMember.setMemberId(imMember.getMemberId());
                if(imGroupMemberService.save(imGroupMember)){
                    return MessageRespResult.success4Data(true);
                }
            }
        }
        return MessageRespResult.error("创建群失败");
    }
    @ApiOperation(value = "自动添加矿工到群")
    @PostMapping("auto/addgroup")
    public MessageRespResult addgroup() {
        List<ImGroup>  imGroups =imService.getAvailableGroup();


        return MessageRespResult.error("添加群成员失败");
    }
    @ApiOperation(value = "添加矿工到聊天室")
    @PostMapping("auto/addchatroom")
    public MessageRespResult addchatroom() {
        ImChatRoom imChatRoom = imChatRoomService.getChatRoom();
        if(imChatRoom!=null){
            if(imService.addchatroom(imChatRoom)){
                List<ImMember> imMembers = imMemberService.getUnJoinChatRoomUser();

            }
        }

        return MessageRespResult.error("添加群成员失败");
    }
    @ApiOperation(value = "添加后台用户到聊天室")
    @PostMapping("auto/addsyschatroom")
    public MessageRespResult addsyschatroom() {
        ImChatRoom imChatRoom = imChatRoomService.getChatRoom();
        if(imChatRoom!=null){
            if(imService.addsyschatroom(imChatRoom)){
                List<SysUser>  sysUsers = sysUserService.getUnJoinChatRoomUser();

            }
        }
        return MessageRespResult.error("添加群成员失败");
    }
}
