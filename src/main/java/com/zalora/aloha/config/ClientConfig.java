package com.zalora.aloha.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.NearCacheMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


/**
 * @author Wolfram Huesken <wolfram.huesken@zalora.com>
 */
@Slf4j
@Component
public class ClientConfig {

    private static final String MODE_CLUSTER = "cluster";

    @Value("${infinispan.cluster.name}")
    private String clusterName;

    @Value("${infinispan.cluster.mode}")
    private String mode;

    @Value("${infinispan.cluster.initialServer}")
    private String initialServer;

    @Value("${infinispan.cache.enabled}")
    private boolean cacheEnabled;

    @Value("${infinispan.cache.maxEntries}")
    private int maxEntries;

    @Getter
    @Value("${infinispan.remote.primaryCacheName}")
    private String primaryCacheName;

    @Getter
    @Value("${infinispan.remote.secondaryCacheName}")
    private String secondaryCacheName;

    @Bean
    public Configuration hotrodConfig() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

        // Cluster Mode
        if (mode.equals(MODE_CLUSTER)) {
            configurationBuilder.addCluster(clusterName).addClusterNode(initialServer, 11222);
        } else {
            configurationBuilder.addServers(initialServer);
        }

        // Near Cache
        if (cacheEnabled) {
            configurationBuilder.nearCache()
                .mode(NearCacheMode.INVALIDATED)
                .maxEntries(maxEntries);
        }

        return configurationBuilder.build();
    }

}
