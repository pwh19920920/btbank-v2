package com.spark.bitrade.biz.impl;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.utils.StringUtils;
import com.spark.bitrade.api.vo.ImResult;
import com.spark.bitrade.biz.ImService;
import com.spark.bitrade.config.bean.ImConfig;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.ImChatRoom;
import com.spark.bitrade.repository.entity.ImGroup;
import com.spark.bitrade.repository.entity.ImMember;
import com.spark.bitrade.repository.entity.MemberVo;
import com.spark.bitrade.repository.service.ImChatRoomService;
import com.spark.bitrade.repository.service.ImGroupService;
import com.spark.bitrade.repository.service.ImMemberService;
import com.spark.bitrade.util.CheckSumBuilder;
import com.spark.bitrade.util.ImUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("imServiceImpl")
public class ImServiceImpl  implements ImService {
    @Autowired
    private ImMemberService imMemberService;
    @Autowired
    private ImGroupService imGroupService;
    @Autowired
    private ImConfig imConfig;
    @Autowired
    private ImChatRoomService imChatRoomService;
    @Override
    public List<MemberVo> getUnRegistImMiner() {
        return imMemberService.getUnRegistImMiner();
    }

    @Override
    public ImMember getImMemberByMemberId(Long memberId) {
        return imMemberService.getImMemberByMemberId(memberId);
    }

    @Override
    public ImMember getImMemberByAcid(String acid) {
        return imMemberService.getImMemberByAcid(acid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImMember syncImMember(MemberVo memberVo) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(ImUtils.registerUrl);
        Date curent = new Date();
        String curTime = String.valueOf((curent).getTime() / 1000L);
        log.info("配置信息{}",imConfig);
        String checkSum = CheckSumBuilder.getCheckSum(imConfig.getAppSecret() , curent.toString() ,curTime);//参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", imConfig.getAppKey());
        httpPost.addHeader("Nonce",  curent.toString());
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        nvps.add(new BasicNameValuePair("accid", memberVo.getType() + memberVo.getMemberId().toString()));
        if(!StringUtils.isEmpty(memberVo.getRealName())){
            nvps.add(new BasicNameValuePair("name", memberVo.getRealName()));
        }else if(!StringUtils.isEmpty(memberVo.getUsername())){
            nvps.add(new BasicNameValuePair("name", memberVo.getUsername()));
        }
        if(!StringUtils.isEmpty(memberVo.getAvatar())){
            nvps.add(new BasicNameValuePair("icon", memberVo.getAvatar()));
        }
        if(!StringUtils.isEmpty(memberVo.getEmail())){
            nvps.add(new BasicNameValuePair("email", memberVo.getEmail()));
        }
        // 执行请求
        HttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            response = httpClient.execute(httpPost);
            ImResult imResult = JSON.parseObject(EntityUtils.toString(response.getEntity(), "utf-8"),ImResult.class);
            if(imResult!=null &&imResult.getCode()!=null&&imResult.getCode().equals(200)){
                ImMember imMember = imResult.getInfo();
                imMember.setMemberId(memberVo.getMemberId());
                return imMember;

            }else{
                log.error("注册用户发生错误，错误信息{}",imResult);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            httpClient.close();
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImResult createGroup(ImGroup imGroup) {

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(ImUtils.createGroup);
        Date curent = new Date();
        String curTime = String.valueOf((curent).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(imConfig.getAppSecret() , curent.toString() ,curTime);//参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", imConfig.getAppKey());
        httpPost.addHeader("Nonce",  curent.toString());
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("tname", imGroup.getTname()));
        nvps.add(new BasicNameValuePair("owner", imGroup.getOwnerAcid()));
        nvps.add(new BasicNameValuePair("icon", imGroup.getIcon()));
        nvps.add(new BasicNameValuePair("announcement", imGroup.getAnnouncement()));
        nvps.add(new BasicNameValuePair("intro", imGroup.getIntro()));
        nvps.add(new BasicNameValuePair("intro", imGroup.getIntro()));
        nvps.add(new BasicNameValuePair("joinmode", "0"));
        nvps.add(new BasicNameValuePair("beinvitemode", "1"));
        nvps.add(new BasicNameValuePair("invitemode", "0"));
        nvps.add(new BasicNameValuePair("uptinfomode", "0"));
        nvps.add(new BasicNameValuePair("upcustommode", "0"));
        nvps.add(new BasicNameValuePair("teamMemberLimit", "500"));
        // 执行请求
        HttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            response = httpClient.execute(httpPost);
            ImResult imResult = JSON.parseObject(EntityUtils.toString(response.getEntity(), "utf-8"),ImResult.class);
            if(imResult!=null &&imResult.getCode()!=null&&imResult.getCode().equals(200)){
                return imResult;

            }else{
                log.error("注册用户发生错误，错误信息{}",imResult);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            httpClient.close();
        }
        return null;
    }

    @Override
    public List<ImGroup> getAvailableGroup() {

        return imGroupService.getAvailableGroup();
    }

    @Override
    public boolean addchatroom(ImChatRoom imChatRoom) {
        return false;
    }

    @Override
    public boolean addsyschatroom(ImChatRoom imChatRoom) {
        return false;
    }

    @Override
    public MemberVo findInfo(Long memberId) {
        ImMember imMember = imMemberService.getImMemberByMemberId(memberId);
        if(imMember==null){
            throw new BtBankException(BtBankMsgCode.NOT_EFECT_MINER);
        }
        //查询聊天室返回聊天室
        ImChatRoom imChatRoom =  imChatRoomService.getChatRoom();
        if(imChatRoom==null){
            throw new BtBankException(BtBankMsgCode.NOT_JOIN_CHAT_ROOM);
        }
        MemberVo memberVo = new MemberVo();
        memberVo.setUserType(imMember.getUserType());
        memberVo.setAccid(imMember.getAccid());
        memberVo.setRealName(imMember.getName());
        memberVo.setUsername(imMember.getName());
        memberVo.setEmail(imMember.getEmail());
        memberVo.setIcon(imMember.getIcon());
        memberVo.setGender(imMember.getGender());
        memberVo.setCreator(imChatRoom.getCreator());
        memberVo.setAnnouncement(imChatRoom.getAnnouncement());
        memberVo.setName(imChatRoom.getName());
        memberVo.setRoomId(imChatRoom.getRoomId());
        memberVo.setToken(imMember.getToken());
        memberVo.setMemberId(imMember.getMemberId());
        return memberVo;
    }

    @Override
    public List<MemberVo> getUnRegistImKefuMiner() {

        return imMemberService.getUnRegistImKefuMiner();
    }
}
