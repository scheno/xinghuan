package com.schening.xinghuan.framework.rocketmq.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

}