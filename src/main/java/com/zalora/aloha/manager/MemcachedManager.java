package com.zalora.aloha.manager;

import com.zalora.aloha.compressor.Compressor;
import com.zalora.aloha.compressor.NoCompressor;
import com.zalora.aloha.config.MemcachedConfig;
import com.zalora.aloha.storage.DefaultInfiniBridge;
import com.zalora.jmemcached.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * @author Wolfram Huesken <wolfram.huesken@zalora.com>
 */
@Slf4j
@Component
public class MemcachedManager {

    private static final String DEFAULT_COMPRESSOR = "com.zalora.aloha.compressor.NoCompressor";

    private final ClientManager clientManager;
    private final MemcachedConfig memcachedConfig;

    @Value("${infinispan.remote.primaryCompression}")
    private String primaryCompressorClass;

    @Value("${infinispan.remote.secondaryCompression}")
    private String secondaryCompressorClass;

    private Compressor primaryCompressor;
    private Compressor secondaryCompressor;

    @Autowired
    public MemcachedManager(ClientManager clientManager, MemcachedConfig memcachedConfig) {
        Assert.notNull(clientManager, "Infinispan Client Manager could not be loaded");
        Assert.notNull(memcachedConfig, "Memcached Config could not be loaded");

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
        initCompressors();

        MemCacheDaemon<LocalCacheElement> mainMemcachedDaemon = new MemCacheDaemon<>();
        mainMemcachedDaemon.setAddr(memcachedConfig.getPrimaryInetSocketAddress());
        mainMemcachedDaemon.setIdleTime(memcachedConfig.getIdleTime());
        mainMemcachedDaemon.setVerbose(memcachedConfig.isVerbose());
        mainMemcachedDaemon.setCache(new CacheImpl(new DefaultInfiniBridge(
            clientManager.getPrimaryCache(), primaryCompressor
        )));

        MemCacheDaemon<LocalCacheElement> sessionMemcachedDaemon = new MemCacheDaemon<>();
        sessionMemcachedDaemon.setAddr(memcachedConfig.getSecondaryInetSocketAddress());
        sessionMemcachedDaemon.setIdleTime(memcachedConfig.getIdleTime());
        sessionMemcachedDaemon.setVerbose(memcachedConfig.isVerbose());
        sessionMemcachedDaemon.setCache(new CacheImpl(new DefaultInfiniBridge(
            clientManager.getSecondaryCache(), secondaryCompressor
        )));

        sessionMemcachedDaemon.start();
        mainMemcachedDaemon.start();
    }

    private void initCompressors() {
        if (primaryCompressorClass.isEmpty()) {
            primaryCompressorClass = DEFAULT_COMPRESSOR;
        }

        if (secondaryCompressorClass.isEmpty()) {
            secondaryCompressorClass = DEFAULT_COMPRESSOR;
        }

        try {
            primaryCompressor = (Compressor) Class.forName(primaryCompressorClass).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            log.error("Could not instantiate {}, falling back to no compression", primaryCompressorClass);
            primaryCompressor = new NoCompressor();
        }

        try {
            secondaryCompressor = (Compressor) Class.forName(secondaryCompressorClass).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            log.error("Could not instantiate {}, falling back to no compression", secondaryCompressorClass);
            secondaryCompressor = new NoCompressor();
        }

        log.info("Primary Compressor: {}", primaryCompressor.getClass().getSimpleName());
        log.info("Secondary Compressor: {}", secondaryCompressor.getClass().getSimpleName());
    }

}
