package com.zalora.aloha.config;

import javax.annotation.PostConstruct;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

/**
 * @author Wolfram Huesken <wolfram.huesken@zalora.com>
 */
@Slf4j
@Component
public class ClientConfig {

    @Getter
    private Configuration hotrodConfiguration;

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
            .addServers(initialServer)
            .build();
    }

}
