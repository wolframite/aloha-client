package com.zalora.aloha.manager;

import com.zalora.aloha.config.ClientConfig;

import javax.annotation.PostConstruct;

import com.zalora.aloha.memcached.MemcachedItem;
import lombok.Getter;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Wolfram Huesken <wolfram.huesken@zalora.com>
 */
@Component
public class ClientManager {

    @Getter
    private RemoteCacheManager remoteCacheManager;

    @Autowired
    private ClientConfig clientConfig;

    @PostConstruct
    public void init() {
        remoteCacheManager = new RemoteCacheManager(clientConfig.getHotrodConfiguration());
    }

    public RemoteCache<String, MemcachedItem> getPrimaryCache() {
        return remoteCacheManager.getCache(clientConfig.getPrimaryCacheName());
    }

    public RemoteCache<String, MemcachedItem> getSecondaryCache() {
        return remoteCacheManager.getCache(clientConfig.getSecondaryCacheName());
    }

}
