package com.schening.xinghuan.shop.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/23 15:23
 */
@MapperScan("com.schening.xinghuan.shop.coupon.repository")
@SpringBootApplication
public class TradeCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeCouponApplication.class);
    }

}
