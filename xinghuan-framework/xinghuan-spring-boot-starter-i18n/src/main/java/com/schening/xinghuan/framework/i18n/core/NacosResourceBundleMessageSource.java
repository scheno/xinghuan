package com.schening.xinghuan.framework.i18n.core;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.exception.NacosException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Data;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.core.io.support.ResourcePropertiesPersister;
import org.springframework.lang.Nullable;
import org.springframework.util.PropertiesPersister;
import org.springframework.util.StringUtils;

/**
 * @author shenchen
 * @version 1.0
 * @date 2023/2/22 11:06
 */
@Data
public class NacosResourceBundleMessageSource extends AbstractResourceBasedMessageSource {

    private boolean concurrentRefresh = true;

    private PropertiesPersister propertiesPersister = ResourcePropertiesPersister.INSTANCE;

    private NacosConfigManager nacosConfigManager;

    private String nacosGroup;

    // Cache to hold filename lists per Locale
    private final ConcurrentMap<String, Map<Locale, List<String>>> cachedFilenames = new ConcurrentHashMap<>();

    // Cache to hold already loaded properties per filename
    private final ConcurrentMap<String, NacosResourceBundleMessageSource.PropertiesHolder> cachedProperties = new ConcurrentHashMap<>();

    // Cache to hold already loaded properties per filename
    private final ConcurrentMap<Locale, NacosResourceBundleMessageSource.PropertiesHolder> cachedMergedProperties = new ConcurrentHashMap<>();

    @Nullable
    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        if (getCacheMillis() < 0) {
            PropertiesHolder propHolder = getMergedProperties(locale);
            MessageFormat result = propHolder.getMessageFormat(code, locale);
            if (result != null) {
                return result;
            }
        } else {
            for (String basename : getBasenameSet()) {
                List<String> filenames = calculateAllFilenames(basename, locale);
                for (String filename : filenames) {
                    PropertiesHolder propHolder = getProperties(filename);
                    MessageFormat result = propHolder.getMessageFormat(code, locale);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 强制刷新
     *
     * @param fileName 文件名
     * @param config   变更后的配置内容
     */
    public void forceRefresh(String fileName, String config) throws IOException {
        synchronized (this) {
            Properties props = newProperties();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8));
            this.propertiesPersister.load(props, inputStream);

            long fileTimestamp = -1;
            NacosResourceBundleMessageSource.PropertiesHolder propHolder = new NacosResourceBundleMessageSource.PropertiesHolder(props, fileTimestamp);
            this.cachedProperties.put(fileName, propHolder);

            this.cachedMergedProperties.clear();
        }
    }

    protected NacosResourceBundleMessageSource.PropertiesHolder getMergedProperties(Locale locale) {
        NacosResourceBundleMessageSource.PropertiesHolder mergedHolder = this.cachedMergedProperties.get(
                locale);
        if (mergedHolder != null) {
            return mergedHolder;
        }

        Properties mergedProps = newProperties();
        long latestTimestamp = -1;
        String[] basenames = StringUtils.toStringArray(getBasenameSet());
        for (int i = basenames.length - 1; i >= 0; i--) {
            List<String> filenames = calculateAllFilenames(basenames[i], locale);
            for (int j = filenames.size() - 1; j >= 0; j--) {
                String filename = filenames.get(j);
                NacosResourceBundleMessageSource.PropertiesHolder propHolder = getProperties(
                        filename);
                if (propHolder.getProperties() != null) {
                    mergedProps.putAll(propHolder.getProperties());
                    if (propHolder.getFileTimestamp() > latestTimestamp) {
                        latestTimestamp = propHolder.getFileTimestamp();
                    }
                }
            }
        }

        mergedHolder = new NacosResourceBundleMessageSource.PropertiesHolder(mergedProps,
                latestTimestamp);
        NacosResourceBundleMessageSource.PropertiesHolder existing = this.cachedMergedProperties.putIfAbsent(
                locale, mergedHolder);
        if (existing != null) {
            mergedHolder = existing;
        }
        return mergedHolder;
    }

    protected List<String> calculateAllFilenames(String basename, Locale locale) {
        Map<Locale, List<String>> localeMap = this.cachedFilenames.get(basename);
        if (localeMap != null) {
            List<String> filenames = localeMap.get(locale);
            if (filenames != null) {
                return filenames;
            }
        }

        // Filenames for given Locale
        List<String> filenames = new ArrayList<>(7);
        filenames.addAll(calculateFilenamesForLocale(basename, locale));

        // Filenames for default Locale, if any
        Locale defaultLocale = getDefaultLocale();
        if (defaultLocale != null && !defaultLocale.equals(locale)) {
            List<String> fallbackFilenames = calculateFilenamesForLocale(basename, defaultLocale);
            for (String fallbackFilename : fallbackFilenames) {
                if (!filenames.contains(fallbackFilename)) {
                    // Entry for fallback locale that isn't already in filenames list.
                    filenames.add(fallbackFilename);
                }
            }
        }

        // Filename for default bundle file
        filenames.add(basename);

        if (localeMap == null) {
            localeMap = new ConcurrentHashMap<>();
            Map<Locale, List<String>> existing = this.cachedFilenames.putIfAbsent(basename,
                    localeMap);
            if (existing != null) {
                localeMap = existing;
            }
        }
        localeMap.put(locale, filenames);
        return filenames;
    }

    protected List<String> calculateFilenamesForLocale(String basename, Locale locale) {
        List<String> result = new ArrayList<>(3);
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        StringBuilder temp = new StringBuilder(basename);

        temp.append('_');
        if (language.length() > 0) {
            temp.append(language);
            result.add(0, temp.toString());
        }

        temp.append('_');
        if (country.length() > 0) {
            temp.append(country);
            result.add(0, temp.toString());
        }

        if (variant.length() > 0 && (language.length() > 0 || country.length() > 0)) {
            temp.append('_').append(variant);
            result.add(0, temp.toString());
        }

        return result;
    }

    protected NacosResourceBundleMessageSource.PropertiesHolder getProperties(String filename) {
        NacosResourceBundleMessageSource.PropertiesHolder propHolder = this.cachedProperties.get(
                filename);
        long originalTimestamp = -2;

        if (propHolder != null) {
            originalTimestamp = propHolder.getRefreshTimestamp();
            if (originalTimestamp == -1
                    || originalTimestamp > System.currentTimeMillis() - getCacheMillis()) {
                // Up to date
                return propHolder;
            }
        } else {
            propHolder = new NacosResourceBundleMessageSource.PropertiesHolder();
            NacosResourceBundleMessageSource.PropertiesHolder existingHolder = this.cachedProperties.putIfAbsent(
                    filename, propHolder);
            if (existingHolder != null) {
                propHolder = existingHolder;
            }
        }

        // At this point, we need to refresh...
        if (this.concurrentRefresh && propHolder.getRefreshTimestamp() >= 0) {
            // A populated but stale holder -> could keep using it.
            if (!propHolder.refreshLock.tryLock()) {
                // Getting refreshed by another thread already ->
                // let's return the existing properties for the time being.
                return propHolder;
            }
        } else {
            propHolder.refreshLock.lock();
        }
        try {
            NacosResourceBundleMessageSource.PropertiesHolder existingHolder = this.cachedProperties.get(
                    filename);
            if (existingHolder != null
                    && existingHolder.getRefreshTimestamp() > originalTimestamp) {
                return existingHolder;
            }
            return refreshProperties(filename, propHolder);
        } finally {
            propHolder.refreshLock.unlock();
        }
    }

    protected NacosResourceBundleMessageSource.PropertiesHolder refreshProperties(String filename,
            @Nullable NacosResourceBundleMessageSource.PropertiesHolder propHolder) {
        long refreshTimestamp = (getCacheMillis() < 0 ? -1 : System.currentTimeMillis());

        long fileTimestamp = -1;

        try {
            Properties props = loadProperties(filename);
            propHolder = new NacosResourceBundleMessageSource.PropertiesHolder(props,
                    fileTimestamp);
        } catch (IOException | NacosException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Could not properties file from nacos", ex);
            }
            // Empty holder representing "not valid".
            propHolder = new NacosResourceBundleMessageSource.PropertiesHolder();
        }

        propHolder.setRefreshTimestamp(refreshTimestamp);
        this.cachedProperties.put(filename, propHolder);
        return propHolder;
    }

    protected Properties loadProperties(String filename) throws IOException, NacosException {
        Properties properties = newProperties();
        String config = nacosConfigManager.getConfigService()
                .getConfig(filename + ".properties", nacosGroup, 5000);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(config.getBytes(
                StandardCharsets.UTF_8));
        this.propertiesPersister.load(properties, byteArrayInputStream);
        return properties;
    }

    /**
     * Template method for creating a plain new {@link Properties} instance. The default
     * implementation simply calls {@link Properties#Properties()}.
     * <p>Allows for returning a custom {@link Properties} extension in subclasses.
     * Overriding methods should just instantiate a custom {@link Properties} subclass, with no
     * further initialization or population to be performed at that point.
     *
     * @return a plain Properties instance
     * @since 4.2
     */
    protected Properties newProperties() {
        return new Properties();
    }

    protected class PropertiesHolder {

        @org.springframework.lang.Nullable
        private final Properties properties;

        private final long fileTimestamp;

        private volatile long refreshTimestamp = -2;

        private final ReentrantLock refreshLock = new ReentrantLock();

        /**
         * Cache to hold already generated MessageFormats per message code.
         */
        private final ConcurrentMap<String, Map<Locale, MessageFormat>> cachedMessageFormats =
                new ConcurrentHashMap<>();

        public PropertiesHolder() {
            this.properties = null;
            this.fileTimestamp = -1;
        }

        public PropertiesHolder(Properties properties, long fileTimestamp) {
            this.properties = properties;
            this.fileTimestamp = fileTimestamp;
        }

        @org.springframework.lang.Nullable
        public Properties getProperties() {
            return this.properties;
        }

        public long getFileTimestamp() {
            return this.fileTimestamp;
        }

        public void setRefreshTimestamp(long refreshTimestamp) {
            this.refreshTimestamp = refreshTimestamp;
        }

        public long getRefreshTimestamp() {
            return this.refreshTimestamp;
        }

        @Nullable
        public String getProperty(String code) {
            if (this.properties == null) {
                return null;
            }
            return this.properties.getProperty(code);
        }

        @Nullable
        public MessageFormat getMessageFormat(String code, Locale locale) {
            if (this.properties == null) {
                return null;
            }
            Map<Locale, MessageFormat> localeMap = this.cachedMessageFormats.get(code);
            if (localeMap != null) {
                MessageFormat result = localeMap.get(locale);
                if (result != null) {
                    return result;
                }
            }
            String msg = this.properties.getProperty(code);
            if (msg != null) {
                if (localeMap == null) {
                    localeMap = new ConcurrentHashMap<>();
                    Map<Locale, MessageFormat> existing = this.cachedMessageFormats.putIfAbsent(
                            code, localeMap);
                    if (existing != null) {
                        localeMap = existing;
                    }
                }
                MessageFormat result = createMessageFormat(msg, locale);
                localeMap.put(locale, result);
                return result;
            }
            return null;
        }
    }

}
