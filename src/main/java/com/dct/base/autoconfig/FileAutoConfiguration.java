package com.dct.base.autoconfig;

import com.dct.base.common.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class FileAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(FileAutoConfiguration.class);
    private static final String ENTITY_NAME = "FileAutoConfiguration";

    @Bean
    @ConditionalOnMissingBean(FileUtils.class)
    public FileUtils fileUtils() {
        log.debug("[{}] - Auto configure default file utils", ENTITY_NAME);
        return new FileUtils();
    }
}
