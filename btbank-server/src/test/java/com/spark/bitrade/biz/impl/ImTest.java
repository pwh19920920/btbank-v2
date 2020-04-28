package com.spark.bitrade.biz.impl;

import com.alibaba.fastjson.JSON;

import com.spark.bitrade.api.vo.ImResult;
import com.spark.bitrade.repository.entity.ImMember;
import com.spark.bitrade.repository.entity.MemberVo;
import com.spark.bitrade.util.CheckSumBuilder;
import com.spark.bitrade.util.ImUtils;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class ImTest {

    @SneakyThrows
    @Test
    public void TestImServer(){
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url = "https://api.netease.im/nimserver/user/create.action";
        HttpPost httpPost = new HttpPost(url);

        String appKey = "8920cb6f373d227733753a556f1dd735";
        String appSecret = "b8da53f2d570";
        String nonce =  "12345";
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce ,curTime);//参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", appKey);
        httpPost.addHeader("Nonce", nonce);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("accid", "helloworld33333"));
        nvps.add(new BasicNameValuePair("name", "哈哈哈"));
        nvps.add(new BasicNameValuePair("email", "helloworld2222@qq.com"));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);

        // 打印执行结果
        System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));



    }
    @Test
    public void TestImServerUtils(){
        String url = "https://api.netease.im/nimserver/user/create.action";
        HttpPost httpPost = new HttpPost(url);

        String appKey = "ef65fe80902d5f5fe7f2f6bbdf3b005f";
        String appSecret = "424f0ecf1eb5";
        String nonce =  "12345";

        MemberVo memberVo = new MemberVo();
        memberVo.setAvatar("http://b-ssl.duitang.com/uploads/item/201802/20/20180220165946_RiGPS.thumb.700_0.jpeg");
        memberVo.setRealName("马茹");
        memberVo.setEmail("1234567@qq.com");
        memberVo.setMemberId(57L);
        memberVo.setUsername("马茹");
        memberVo.setType("sys");
        ImMember imber = ImUtils.syncMember(memberVo,appKey,appSecret);
        System.out.println(JSON.toJSONString(imber));
    }
    @Test
    public void TestcreateChatRoom(){
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(ImUtils.createChatRoom);

        String appKey = " 8920cb6f373d227733753a556f1dd735";
        String appSecret = "b8da53f2d570";
        String nonce =  "12345";
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce ,curTime);//参考 计算CheckSum的java代码


        // 设置请求的header
        httpPost.addHeader("AppKey", appKey);
        httpPost.addHeader("Nonce", nonce);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("creator", "1"));
        nvps.add(new BasicNameValuePair("name", "test"));
        nvps.add(new BasicNameValuePair("announcement", "test"));
        nvps.add(new BasicNameValuePair("queuelevel", "1"));
        HttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            response = httpClient.execute(httpPost);
            ImResult imResult = JSON.parseObject(EntityUtils.toString(response.getEntity(), "utf-8"),ImResult.class);
            System.out.println(imResult);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            httpClient.close();
        }

    }
    //修改用户名
    @Test
    public void TestRename(){
       String namejson = "[{\"accid\":\"kf410364\",\"name\":\"官方发言人\"},{\"accid\":\"kf410365\",\"name\":\"客服02\"},{\"accid\":\"kf410366\",\"name\":\"客服03\"},{\"accid\":\"kf410367\",\"name\":\"客服04\"},{\"accid\":\"kf410368\",\"name\":\"客服05\"},{\"accid\":\"kf410369\",\"name\":\"客服06\"},{\"accid\":\"kf410370\",\"name\":\"客服07\"},{\"accid\":\"kf410371\",\"name\":\"客服08\"},{\"accid\":\"kf410372\",\"name\":\"客服09\"},{\"accid\":\"kf410373\",\"name\":\"客服10\"},{\"accid\":\"kf410374\",\"name\":\"客服11\"},{\"accid\":\"kf410375\",\"name\":\"客服12\"},{\"accid\":\"kf410376\",\"name\":\"客服13\"},{\"accid\":\"kf410377\",\"name\":\"客服14\"},{\"accid\":\"kf410378\",\"name\":\"客服15\"},{\"accid\":\"kf410379\",\"name\":\"客服010\"},{\"accid\":\"kf410380\",\"name\":\"客服011\"},{\"accid\":\"kf410381\",\"name\":\"客服012\"},{\"accid\":\"kf410382\",\"name\":\"客服013\"},{\"accid\":\"kf410383\",\"name\":\"客服014\"},{\"accid\":\"kf410383\",\"name\":\"客服015\"}]";

       List<NamePare> NamePares = JSON.parseArray(namejson,NamePare.class);
       for (NamePare NamePare:NamePares){
           System.out.println(JSON.toJSONString(NamePare));
           rename(NamePare.getAccid(),NamePare.getName());
       }

    }
    //修改用户名
    @Test
    public void TestRename1(){
        rename("kf410364","客服01");
    }

    public void rename(String accid, String name){
        String url = "https://api.netease.im/nimserver/user/updateUinfo.action";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("https://api.netease.im/nimserver/user/updateUinfo.action");

        String appKey = " ef65fe80902d5f5fe7f2f6bbdf3b005f";
        String appSecret = "424f0ecf1eb5";
        String nonce =  "12345";
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce ,curTime);//参考 计算CheckSum的java代码


        // 设置请求的header
        httpPost.addHeader("AppKey", appKey);
        httpPost.addHeader("Nonce", nonce);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("accid", accid));
        nvps.add(new BasicNameValuePair("name", name));
        HttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            response = httpClient.execute(httpPost);
            ImResult imResult = JSON.parseObject(EntityUtils.toString(response.getEntity(), "utf-8"),ImResult.class);
            System.out.println(imResult);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            httpClient.close();
        }

    }


}
@Data
class NamePare{
    private String accid;
    private String  name;
}
