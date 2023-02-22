package com.schening.xinghuan.framework.i18n.config;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.schening.xinghuan.framework.i18n.core.NacosResourceBundleMessageSource;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/2/22 12:38
 */
@Slf4j
@AutoConfiguration
public class XinghuanI18nAutoConfiguration {

    @Value("${spring.messages.encoding}")
    private String encoding;

    @Value("${spring.cloud.nacos.discovery.group}")
    private String group;

    @Resource
    private NacosConfigManager nacosConfigManager;

    @Bean("messageSource")
    NacosResourceBundleMessageSource messageSource() {
        NacosResourceBundleMessageSource messageSource = new NacosResourceBundleMessageSource();
        messageSource.setBasenames("message");
        messageSource.setDefaultEncoding(encoding);
        messageSource.setNacosGroup(group);
        messageSource.setNacosConfigManager(nacosConfigManager);
        messageSource.setCacheSeconds(10);
        return messageSource;
    }

}
