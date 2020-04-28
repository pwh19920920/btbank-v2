package com.spark.bitrade.util;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.api.vo.ImResult;
import com.spark.bitrade.repository.entity.ImMember;
import com.spark.bitrade.repository.entity.MemberVo;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImUtils {

    public static final String registerUrl = "https://api.netease.im/nimserver/user/create.action";
    public static final String createGroup = "https://api.netease.im/nimserver/team/create.action";
    public static final String inviteGroup = "https://api.netease.im/nimserver/team/add.action";

    public static final String createChatRoom = "https://api.netease.im/nimserver/chatroom/create.action";
    public static ImMember syncMember(MemberVo memberVo, String appKey, String appSecret) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(registerUrl);
        Date curent = new Date();
        String nonce =  curent.toString();
        String curTime = String.valueOf((curent).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce ,curTime);//参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", appKey);
        httpPost.addHeader("Nonce", nonce);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("accid", memberVo.getMemberId().toString()));
        nvps.add(new BasicNameValuePair("name", memberVo.getRealName()));
        nvps.add(new BasicNameValuePair("icon", memberVo.getAvatar()));
        nvps.add(new BasicNameValuePair("email", memberVo.getEmail()));
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
}
