package com.schening.xinghuan.framework.banner.config;

import com.schening.xinghuan.framework.banner.core.BannerApplicationRunner;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/2/8 16:42
 */
@AutoConfiguration
public class XinghuanBannerAutoConfiguration {

    @Bean
    public ApplicationRunner getApplicationRunner() {
        return new BannerApplicationRunner();
    }

}
