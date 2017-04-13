package com.zalora.aloha.manager;

import com.zalora.aloha.config.MemcachedConfig;
import com.zalora.aloha.storage.DefaultInfiniBridge;
import com.zalora.jmemcached.CacheImpl;
import com.zalora.jmemcached.LocalCacheElement;
import com.zalora.jmemcached.MemCacheDaemon;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author Wolfram Huesken <wolfram.huesken@zalora.com>
 */
@Component
public class MemcachedManager {

    private final ClientManager clientManager;
    private final MemcachedConfig memcachedConfig;

    @Autowired
    public MemcachedManager(ClientManager clientManager, MemcachedConfig memcachedConfig) {
        Assert.notNull(clientManager, "Infinispan Client Manager could not be loaded");

        Assert.notNull(
            memcachedConfig.getPrimaryInetSocketAddress(),
            "Main Memcached listen address is not configured"
        );

        Assert.notNull(
            memcachedConfig.getSecondaryInetSocketAddress(),
            "Session Memcached listen address is not configured"
        );

        this.clientManager = clientManager;
        this.memcachedConfig = memcachedConfig;
    }

    @PostConstruct
    public void init() {
        MemCacheDaemon<LocalCacheElement> mainMemcachedDaemon = new MemCacheDaemon<>();
        mainMemcachedDaemon.setAddr(memcachedConfig.getPrimaryInetSocketAddress());
        mainMemcachedDaemon.setIdleTime(memcachedConfig.getIdleTime());
        mainMemcachedDaemon.setVerbose(memcachedConfig.isVerbose());
        mainMemcachedDaemon.setCache(
            new CacheImpl(new DefaultInfiniBridge(clientManager.getPrimaryCache())
        ));

        MemCacheDaemon<LocalCacheElement> sessionMemcachedDaemon = new MemCacheDaemon<>();
        sessionMemcachedDaemon.setAddr(memcachedConfig.getSecondaryInetSocketAddress());
        sessionMemcachedDaemon.setIdleTime(memcachedConfig.getIdleTime());
        sessionMemcachedDaemon.setVerbose(memcachedConfig.isVerbose());
        sessionMemcachedDaemon.setCache(
            new CacheImpl(new DefaultInfiniBridge(clientManager.getSecondaryCache())
        ));

        sessionMemcachedDaemon.start();
        mainMemcachedDaemon.start();
    }

}
