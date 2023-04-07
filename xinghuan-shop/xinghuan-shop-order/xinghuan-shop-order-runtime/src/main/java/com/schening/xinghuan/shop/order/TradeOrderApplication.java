package com.schening.xinghuan.shop.order;

import com.schening.xinghuan.shop.common.utils.IDWorker;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * @author shenchen
 */
@EnableFeignClients("com.schening.xinghuan.shop")
@SpringBootApplication
@MapperScan("com.schening.xinghuan.shop.order.repository")
public class TradeOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeOrderApplication.class);
    }

    @Bean
    IDWorker idWorker() {
        return new IDWorker(1, 4);
    }

}
