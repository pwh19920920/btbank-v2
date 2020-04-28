package com.spark.bitrade.config.bean;

import lombok.Data;

/**
 * OpenEpmOptions
 *
 * @author biu
 * @since 2019/12/25 16:18
 */
@Data
public class OpenEpmOptions {

    /**
     * 验证开关
     */
    private boolean valid = false;

    /**
     * 签名超时时间
     */
    private int timeoutSeconds = 3;

    /**
     * 签名密钥
     */
    private String secret = "#Emp2019BtBank#";
}
