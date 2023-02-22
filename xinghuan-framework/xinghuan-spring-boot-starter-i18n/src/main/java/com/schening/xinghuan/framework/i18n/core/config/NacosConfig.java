package com.schening.xinghuan.framework.i18n.core.config;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.collect.Lists;
import com.schening.xinghuan.framework.i18n.core.NacosResourceBundleMessageSource;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/2/22 12:44
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class NacosConfig {

    @Value("${spring.cloud.nacos.discovery.group}")
    private String group;

    private final LocaleProperties localeProperties;

    private final NacosConfigManager nacosConfigManager;

    private final NacosResourceBundleMessageSource messageSource;

    @PostConstruct
    public void init() throws NacosException {
        //1.从nacos获取国际化文件名，遍历每个文件名
        List<String> fileNameList = localeProperties.getFilename();
        if(ObjectUtil.isEmpty(fileNameList)){
            fileNameList = Lists.newArrayList("message_zh_CN", "message_en_US");
        }
        // 2.遍历每个文件名，添加nacos监听器，监听对应配置变化
        for (String fileName : fileNameList) {
            // nacos查询配置是以文件名来查询
            String dataId = fileName + ".properties";
            nacosConfigManager.getConfigService().addListener(dataId, group, new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    try {
                        // 更新对应配置信息，可以带入对应时区信息，做到仅更新对应时区的信息，避免全量的开销
                        messageSource.forceRefresh(fileName, configInfo);
                    } catch (Exception e) {
                        log.error("国际化配置监听异常", e);
                    }
                }

                @Override
                public Executor getExecutor() {
                    return null;
                }
            });
        }
        log.info("国际化初始配置结束");
    }
    /**
     * @return LocaleResolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        return new LocaleResolver() {
            @Override
            public Locale resolveLocale(HttpServletRequest request) {
                // 通过请求头的lang参数解析locale
                String temp = request.getHeader("lang");
                if (!StringUtils.isEmpty(temp)) {
                    String[] split = temp.split("_");
                    // 构造器要用对，不然时区对象在MessageSource实现类中获取的fileName将与传入的配置信息不匹配导致问题
                    Locale locale = new Locale(split[0], split[1]);
                    log.info("locale:" + locale);
                    return locale;
                } else {
                    return Locale.getDefault();
                }
            }

            @Override
            public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {
            }
        };
    }

}
