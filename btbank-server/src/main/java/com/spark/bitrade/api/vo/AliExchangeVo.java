package com.spark.bitrade.api.vo;

import lombok.Data;

import java.util.Date;
@Data
public class AliExchangeVo {
    private String hui_in;
    private String chao_out;
    private String time;
    private String chao_in;
    private String hui_out;
    private String name;
    private String zhesuan;
    private Date day;
    private String code;
}
