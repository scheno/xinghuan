package com.schening.xinghuan.shop.goods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 17:19
 */
@MapperScan("com.schening.xinghuan.shop.goods.repository")
@SpringBootApplication
public class TradeGoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeGoodsApplication.class);
    }

}
