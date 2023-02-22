package com.schening.xinghuan.framework.i18n.core.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/2/22 12:49
 */
@Data
@Component
//@Configuration
@ConfigurationProperties(prefix = "locale")
public class LocaleProperties {

    private List<String> filename;

}
