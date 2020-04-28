package com.spark.bitrade.api.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.spark.bitrade.constant.MinerGrade;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

//import jdk.nashorn.internal.objects.annotations.Setter;

/**
 * @author ww
 * @time 2019.11.12 09:26
 */

@Data
@ApiModel("用户推荐信息类")
public class MinerRecommandVO {


    @TableField(value = "mobile_phone")
    @ApiModelProperty("推荐人手机号码")
    String mobilePhone = "";


    @TableField(value = "miner_grade")
    @ApiModelProperty("推荐人等级")
    Integer minerGrade = MinerGrade.SILVER_MINER.getGradeId();

    @TableField(value = "username")
    @ApiModelProperty("推荐人昵称")
    String username = "";

    @TableField(value = "registration_time")
    @ApiModelProperty("推荐人注册时间")
    Date registrationTime = new Date();

    @TableField(value = "is_miner")
    @ApiModelProperty("是否是旷工")
    int isMiner = 0;


//    @Setter
//    public void setMobilePhone(String mobilePhone) {
//        if (null != mobilePhone) {
//            this.mobilePhone = mobilePhone.replaceFirst("^(\\d*)\\d{4}(\\d{4})$", "$1****$2");
//        }
//    }
//
//    @Setter
//    public void setUsername(String username) {
//        if (null != username) {
//            this.username = username;
//            this.username = this.username.replaceFirst("^(\\d*)\\d{4}(\\d{4})$", "$1****$2");
//            this.username = this.username.replaceFirst("^(.{1,3}).*(\\@([\\w-_]+\\.)+([\\w-_]+))$", "$1****$2");
//        }
//    }

}
