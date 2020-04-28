package com.spark.bitrade.facea;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class AuthenticationDto {

    @Id
    private String id;

    /**
     * UserID
     */
    private String uid;

    /**
     * ID Card
     */
    private String idNumber;

    /**
     * Real Name
     */
    private String realName;

    /**
     * Face image base64
     */
    private String image64;

}
