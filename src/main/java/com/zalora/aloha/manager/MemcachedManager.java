package com.zalora.aloha.manager;

import com.zalora.aloha.compressor.Compressor;
import com.zalora.aloha.compressor.NoCompressor;
import com.zalora.aloha.config.MemcachedConfig;
import com.zalora.aloha.memcached.MemcachedItem;
import com.zalora.aloha.storage.DefaultInfiniBridge;
import com.zalora.jmemcached.*;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.client.hotrod.RemoteCache;
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

    private static final String DEFAULT_COMPRESSOR = "com.zalora.aloha.compressor.NoCompressor";

    @Autowired
    private ClientManager clientManager;

    @Autowired
    private MemcachedConfig memcachedConfig;

    @Autowired
    private InetSocketAddress socketAddress;

    @Autowired
    private RemoteCache<String, MemcachedItem> mainCache;

    @Value("${infinispan.remote.compression}")
    private String compressorClass;

    private Compressor compressor;

    @PostConstruct
    public void init() {
        initCompressors();

        MemCacheDaemon<LocalCacheElement> mainMemcachedDaemon = new MemCacheDaemon<>();
        mainMemcachedDaemon.setAddr(socketAddress);
        mainMemcachedDaemon.setIdleTime(memcachedConfig.getIdleTime());
        mainMemcachedDaemon.setVerbose(memcachedConfig.isVerbose());
        mainMemcachedDaemon.setCache(new CacheImpl(new DefaultInfiniBridge(mainCache, compressor)));

        mainMemcachedDaemon.start();
    }

    private void initCompressors() {
        if (compressorClass.isEmpty()) {
            compressorClass = DEFAULT_COMPRESSOR;
        }

        try {
            compressor = (Compressor) Class.forName(compressorClass).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            log.error("Could not instantiate {}, falling back to no compression", compressorClass);
            compressor = new NoCompressor();
        }

        log.info("Compressor: {}", compressor.getClass().getSimpleName());
    }

}
