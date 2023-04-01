package com.schening.xinghuan.shop.user.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 15:06
 */
@Data
@TableName("trade_user")
public class TradeUserPO implements Serializable {

    @TableField("user_id")
    private Long userId;

    @TableField("user_name")
    private String userName;

    @TableField("user_password")
    private String userPassword;

    @TableField("user_mobile")
    private String userMobile;

    @TableField("user_score")
    private Integer userScore;

    @TableField("user_reg_time")
    private Date userRegTime;

    @TableField("user_money")
    private Long userMoney;

}
