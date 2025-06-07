package com.dct.base.autoconfig;

import com.dct.base.common.FileUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class FileAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(FileUtils.class)
    public FileUtils fileUtils() {
        return new FileUtils();
    }
}
