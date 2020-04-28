package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * 换汇银行卡(ForeignMemberBankinfo)实体类
 *
 * @author qiuyuanjie
 * @since 2020-02-04 10:42:09
 */
@ApiModel(description = "换汇银行卡表")
@Data
@TableName(value = "foreign_member_bankinfo")
public class ForeignMemberBankinfo implements Serializable {
    private static final long serialVersionUID = 684965233541256894L;

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;
    //用户id
    @ApiModelProperty(value = "用户ID", example = "")
    private Long memberId;
    //账户姓名
    @ApiModelProperty(value = "账户姓名", example = "")
    private String accountName;
    //开户行
    @ApiModelProperty(value = "开户行", example = "")
    private String bankName;
    //swift代码
    @ApiModelProperty(value = "swift代码", example = "")
    private String swiftCode;
    //账号
    @ApiModelProperty(value = "银行账号", example = "")
    private String accountNumber;
    //开户行地址
    @ApiModelProperty(value = "开户行地址", example = "")
    private String bankAddress;
    //创建时间
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;
    //更新时间
    @ApiModelProperty(value = "更新时间", example = "")
    private Date updateTime;



}