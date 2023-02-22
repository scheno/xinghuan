package com.schening.xinghuan.server;

//import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/2/8 16:58
 */
//@MapperScan(basePackages = "com.schening.xinghuan")
@SpringBootApplication
@ComponentScan(basePackages = "com.schening.xinghuan")
@EnableConfigurationProperties
public class XinghuanServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(XinghuanServerApplication.class);
    }
}
