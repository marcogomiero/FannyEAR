package com.platformteam.config;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.CacheContainerAdmin;
import org.infinispan.manager.DefaultCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfinispanConfig {

    @Bean
    public DefaultCacheManager cacheManager() {
        // Set up a clustered Cache Manager.
        GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();
        // Initialize the default Cache Manager.
        DefaultCacheManager cacheManager = new DefaultCacheManager(global.build());

        // Create a distributed cache with synchronous replication.
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.clustering().cacheMode(CacheMode.DIST_SYNC);

        // Obtain a volatile cache.
        cacheManager.administration().withFlags(CacheContainerAdmin.AdminFlag.VOLATILE).getOrCreateCache("myCache", builder.build());

        return cacheManager;
    }

    @Bean
    public Cache<String, String> cache(DefaultCacheManager cacheManager) {
        return cacheManager.getCache("myCache");
    }
}
