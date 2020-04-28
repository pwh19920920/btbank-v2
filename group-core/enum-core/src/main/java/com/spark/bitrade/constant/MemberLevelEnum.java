package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author rongyu
 * @description 会员等级
 * @date 2017/12/25 17:03
 */
@AllArgsConstructor
@Getter
public enum MemberLevelEnum implements BaseEnum{
    GENERAL( "普通"),
    REALNAME("实名"),
    IDENTIFICATION("认证商家");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }


}
