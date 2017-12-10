package com.zalora.aloha.manager;

import com.zalora.aloha.config.MemcachedConfig;
import com.zalora.aloha.memcached.MemcachedItem;
import com.zalora.aloha.storage.DefaultInfiniBridge;
import com.zalora.jmemcached.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import javax.annotation.PostConstruct;

/**
 * @author Wolfram Huesken <wolfram.huesken@zalora.com>
 */
@Slf4j
@Component
public class MemcachedManager {

    @Value("${aloha.remote.cacheName}")
    private String cacheName;

    @Autowired
    private MemcachedConfig memcachedConfig;

    @Autowired
    private InetSocketAddress inetSocketAddress;

    @Autowired
    private Ignite ignite;

    @PostConstruct
    public void init() {
        IgniteCache<String, MemcachedItem> cache = ignite.getOrCreateCache(cacheName);

        MemcachedDaemon<LocalCacheElement> mainMemcachedDaemon = new MemcachedDaemon<>();
        mainMemcachedDaemon.setAddr(inetSocketAddress);
        mainMemcachedDaemon.setIdleTime(memcachedConfig.getIdleTime());
        mainMemcachedDaemon.setVerbose(memcachedConfig.isVerbose());
        mainMemcachedDaemon.setCache(new CacheImpl(new DefaultInfiniBridge(cache)));

        mainMemcachedDaemon.start();
    }

}
