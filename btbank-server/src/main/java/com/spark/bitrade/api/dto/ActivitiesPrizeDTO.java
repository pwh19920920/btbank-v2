package com.spark.bitrade.api.dto;

import com.spark.bitrade.repository.entity.TurntablePrize;
import lombok.Data;

import java.io.Serializable;

/**
 * ActivitiesPrizeDTO
 *
 * @author biu
 * @since 2020/1/8 16:03
 */
@Data
public class ActivitiesPrizeDTO implements Serializable {

    private Integer id;
    private String name;
    private String image;
    private Integer priority;
    private Integer stock;
    private Double rate;
    private Integer toplimit;
    private Double amount;

    // NONE -> 参与奖, BT -> BT, OBJECT -> 实物
    private String type;

    public boolean isNotNone() {
        return !"NONE".equals(type);
    }

    public static ActivitiesPrizeDTO instanceOf(TurntablePrize prize) {
        ActivitiesPrizeDTO dto = new ActivitiesPrizeDTO();

        dto.setId(prize.getId());
        dto.setName(prize.getName());
        dto.setImage(prize.getImage());
        dto.setPriority(prize.getPriority());
        dto.setStock(prize.getStock());
        dto.setRate(prize.getRate());
        dto.setToplimit(prize.getToplimit());
        dto.setAmount(prize.getAmount());

        dto.setType(prize.getType());

        return dto;
    }

    public static ActivitiesPrizeDTO noneOf(double rate) {
        ActivitiesPrizeDTO dto = new ActivitiesPrizeDTO();

        dto.setId(0);
        dto.setName("谢谢参与");
        dto.setToplimit(0);
        dto.setStock(0);
        dto.setRate(rate);
        dto.setType("NONE");

        return dto;
    }

    public static ActivitiesPrizeDTO noneOfEight(double rate) {
        ActivitiesPrizeDTO dto = new ActivitiesPrizeDTO();
        dto.setId(0);
        dto.setName("8BT");
        dto.setToplimit(0);
        dto.setStock(0);
        dto.setAmount(8.0);
        dto.setRate(rate);
        dto.setPriority(0);
        dto.setType("BT");
        return dto;
    }
}

