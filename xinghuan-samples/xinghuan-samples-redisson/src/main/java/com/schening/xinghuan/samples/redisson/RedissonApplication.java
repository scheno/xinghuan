package com.schening.xinghuan.samples.redisson;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/5/11 16:45
 */
@MapperScan("com.schening.xinghuan")
@SpringBootApplication
public class RedissonApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedissonApplication.class);
    }

}
