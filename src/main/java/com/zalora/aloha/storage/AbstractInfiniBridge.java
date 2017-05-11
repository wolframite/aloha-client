package com.zalora.aloha.storage;

import com.zalora.jmemcached.LocalCacheElement;
import com.zalora.jmemcached.storage.CacheStorage;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.ServerStatistics;
import org.springframework.util.Assert;
import java.io.IOException;
import java.util.*;

/**
 * Hook up jMemcached and Infinispan
 *
 * @author Wolfram Huesken <wolfram.huesken@zalora.com>
 */
@Slf4j
public abstract class AbstractInfiniBridge implements CacheStorage<String, LocalCacheElement> {

    private RemoteCache<String, ?> ispanCache;

    AbstractInfiniBridge(RemoteCache<String, ?> ispanCache) {
        Assert.notNull(ispanCache, "Infinispan Cache must not be null");
        this.ispanCache = ispanCache;
    }

    @Override
    public long getMemoryCapacity() {
        return Runtime.getRuntime().maxMemory();
    }

    /**
     * Return a rough estimate instead of zero
     */
    @Override
    public long getMemoryUsed() {
        return Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory();
    }

    /**
     * Removed for performance reasons
     */
    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public void close() throws IOException {
        ispanCache.stop();
    }

    /**
     * Removed for performance reasons
     */
    @Override
    public int size() {
        return ispanCache.stats().getIntStatistic(ServerStatistics.CURRENT_NR_OF_ENTRIES);
    }

    @Override
    public void clear() {
        ispanCache.clear();
        log.warn("Flushed {} cache", ispanCache.getName());
    }

    @Override
    public boolean isEmpty() {
        return ispanCache.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return ispanCache.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return ispanCache.containsValue(value);
    }

    @Override
    public LocalCacheElement remove(Object key) {
        ispanCache.remove((String) key);
        return null;
    }

    /**
     * Only replace by key is in use
     */
    @Override
    public boolean replace(String key, LocalCacheElement searchElement, LocalCacheElement replaceElement) {
        return false;
    }

    // The memcached protocol does not support those operations, so they're not implemented here
    @Override
    public void putAll(Map<? extends String, ? extends LocalCacheElement> map) {
    }

    @Override
    public Set<String> keySet() {
        return new HashSet<>();
    }

    @Override
    public Collection<LocalCacheElement> values() {
        return new HashSet<>();
    }

    @Override
    public Set<Entry<String, LocalCacheElement>> entrySet() {
        return new HashSet<>();
    }

}
