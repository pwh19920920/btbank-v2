package com.spark.bitrade.api.vo;

import com.spark.bitrade.repository.entity.ImMember;
import lombok.Data;

@Data
public class ImResult {
    private Integer code;
    private ImMember info;
    private String tid;
    private Faccid faccid;
}



