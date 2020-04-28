package com.spark.bitrade;

/**
 *  
 *
 * @author yangch
 * @time 2019.05.08 20:35
 */
public class Test {
    public static void main(String[] args) {
        String qrCode = "http://silktraderpriv.oss-cn-hongkong.aliyuncs.com/2019/05/08/55f85f34-7bf6-4bd3-a8e4-d2cd989c14d3.jpg?Expires=1557302406&OSSAccessKeyId=LTAIgslrcDq69ahL&Signature=s%2FKWnL2K7zYwr%2F%2BPSn5p7OPvlno%3D";
        System.out.println(qrCode.split("[?]")[0].split("[|/]", 4)[3]);
    }
}
