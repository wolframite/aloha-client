package com.zalora.aloha.manager;

import com.zalora.aloha.config.ClientConfig;
import com.zalora.aloha.memcached.MemcachedItem;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Wolfram Huesken <wolfram.huesken@zalora.com>
 */
@Component
public class ClientManager {

    private final ClientConfig clientConfig;
    private final Configuration hotrodConfig;

    @Autowired
    public ClientManager(Configuration hotrodConfig, ClientConfig clientConfig) {
        this.hotrodConfig = hotrodConfig;
        this.clientConfig = clientConfig;
    }

    @Bean
    public RemoteCacheManager remoteCacheManager() {
        return new RemoteCacheManager(hotrodConfig);
    }

    @Bean
    public RemoteCache<String, MemcachedItem> mainCache(RemoteCacheManager remoteCacheManager) {
        return remoteCacheManager.getCache(clientConfig.getCacheName());
    }

}
