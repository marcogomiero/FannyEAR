package com.platformteam.config;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class InfinispanConfig {

    @Bean
    @Primary
    public org.infinispan.configuration.cache.Configuration defaultCacheConfiguration() {
        return new ConfigurationBuilder()
                .expiration()
                .lifespan(60000)
                .build();
    }
}
