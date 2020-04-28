package com.spark.bitrade.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spark.bitrade.repository.entity.TurntableWinning;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * ActivitiesCarouselVO
 *
 * @author biu
 * @since 2020/1/10 15:37
 */
@Data
@ApiModel("中奖记录轮播")
public class ActivitiesCarouselVO {

    @ApiModelProperty("用户")
    private String user;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("奖品")
    private String prize;

    @ApiModelProperty("中奖时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

    public static ActivitiesCarouselVO instanceOf(TurntableWinning winning) {
        ActivitiesCarouselVO vo = new ActivitiesCarouselVO();

        vo.setUser(winning.getRealName());

        String mobilePhone = winning.getMobilePhone();
        if (StringUtils.hasText(mobilePhone)) {
            char[] chars = mobilePhone.toCharArray();
            int len = chars.length;
            StringBuilder buf = new StringBuilder(len);
            for (int i = 0; i < chars.length; i++) {
                if (i < 3 || i > 6) {
                    buf.append(chars[i]);
                } else {
                    buf.append("*");
                }
            }
            vo.setPhone(buf.toString());
        }
        vo.setPrize(winning.getPrizeName());
        vo.setTime(winning.getCreateTime());

        return vo;
    }
}
