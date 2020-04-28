package com.spark.bitrade.entity.chat;

import lombok.Data;

/**
 * @author: Zhong Jiang
 * @date: 2020-03-23 15:33
 */
@Data
public class ImResult {

    private String code;

    private String tid;

    private String desc;

    private Info info;

}

@Data
class Info {

    private String accid;

    private String token;

    private String name;

}
