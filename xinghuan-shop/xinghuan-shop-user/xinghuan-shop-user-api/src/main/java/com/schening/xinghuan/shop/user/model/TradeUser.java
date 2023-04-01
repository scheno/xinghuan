package com.schening.xinghuan.shop.user.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 15:03
 */
@Data
public class TradeUser implements Serializable {

    private Long userId;

    private String userName;

    private String userPassword;

    private String userMobile;

    private Integer userScore;

    private Date userRegTime;

    private Long userMoney;

}
