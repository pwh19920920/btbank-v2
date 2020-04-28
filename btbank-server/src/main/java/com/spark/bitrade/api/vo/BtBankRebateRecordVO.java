package com.spark.bitrade.api.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.spark.bitrade.repository.entity.BtBankRebateRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ww
 * @time 2019.11.12 16:22
 */


@ApiModel("被推荐人列表")
@Data
public class BtBankRebateRecordVO extends BtBankRebateRecord {


    @TableField("username")
    @ApiModelProperty("被推荐人用户名")
    private String username = "";

    private void setUsername(String username) {
        if (null != username) {
            this.username = username;
            this.username = this.username.replaceFirst("^(\\d*)\\d{4}(\\d{4})$", "$1****$2");
            this.username = this.username.replaceFirst("^(.{1,3}).*(\\@([\\w-_]+\\.)+([\\w-_]+))$", "$1****$2");
        }
    }

}
