package com.zalora.aloha.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Wolfram Huesken <wolfram.huesken@zalora.com>
 */
@Slf4j
@Component
public class ClientConfig {

    @Getter
    private Configuration hotrodConfiguration;

    @Value("${infinispan.cluster.name}")
    private String clusterName;

    @Value("${infinispan.cluster.initialServer}")
    private String initialServer;

    @Getter
    @Value("${infinispan.remote.primaryCacheName}")
    private String primaryCacheName;

    @Getter
    @Value("${infinispan.remote.secondaryCacheName}")
    private String secondaryCacheName;

    @PostConstruct
    public void init() {
        hotrodConfiguration = new ConfigurationBuilder()
            .addCluster(clusterName).addClusterNode(initialServer, 11222)
            .build();
    }

}
