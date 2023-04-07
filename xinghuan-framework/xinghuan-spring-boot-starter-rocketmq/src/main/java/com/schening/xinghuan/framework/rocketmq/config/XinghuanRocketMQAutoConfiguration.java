package com.schening.xinghuan.framework.rocketmq.config;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/3/22 16:39
 */
@Configuration
@EnableConfigurationProperties({MQProperties.class})
@AutoConfigureOrder // 最低优先级配置
public class XinghuanRocketMQAutoConfiguration {

    @Autowired
    private MQProperties properties;

//    @Bean
    DefaultMQProducer producer() {
        return new DefaultMQProducer();
    }

//    @Bean
//    @ConditionalOnMissingBean(RocketMQTemplate.class)
    RocketMQTemplate rocketMQTemplate() {
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        rocketMQTemplate.setProducer(producer());
        return new RocketMQTemplate();
    }

}
