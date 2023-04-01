package com.schening.xinghuan.shop.pay;

import com.schening.xinghuan.shop.common.utils.IDWorker;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@MapperScan("com.schening.xinghuan.shop.pay.repository")
@SpringBootApplication
public class TradePayApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradePayApplication.class);
    }

    @Bean
    public IDWorker getBean() {
        return new IDWorker(1, 2);
    }

    @Bean
    public ThreadPoolTaskExecutor getThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("Pool-Pay");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;

    }

}
