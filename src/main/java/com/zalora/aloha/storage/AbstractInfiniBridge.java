package com.zalora.aloha.storage;

import com.zalora.jmemcached.LocalCacheElement;
import com.zalora.jmemcached.storage.CacheStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteCache;
import org.springframework.util.Assert;
import java.io.IOException;

/**
 * Hook up jMemcached and Ignite
 *
 * @author Wolfram Huesken <wolfram.huesken@zalora.com>
 */
@Slf4j
public abstract class AbstractInfiniBridge implements CacheStorage<String, LocalCacheElement> {

    private IgniteCache<String, ?> igniteCache;

    AbstractInfiniBridge(IgniteCache<String, ?> igniteCache) {
        Assert.notNull(igniteCache, "Ignite Cache must not be null");
        this.igniteCache = igniteCache;
    }

    @Override
    public void close() {
        igniteCache.close();
    }

    @Override
    public int size() {
        return igniteCache.metrics().getSize();
    }

    @Override
    public void clear() {
        igniteCache.clear();
        log.warn("Flushed {} cache", igniteCache.getName());
    }

    public boolean containsKey(String key) {
        return igniteCache.containsKey(key);
    }

    @Override
    public boolean remove(String key) {
        return igniteCache.remove(key);
    }

}
