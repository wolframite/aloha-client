package com.zalora.aloha.storage;

import com.zalora.aloha.beans.MemcachedItem;
import com.zalora.jmemcached.LocalCacheElement;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteCache;
import org.jboss.netty.buffer.ChannelBuffers;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Hook up jMemcached and Infinispan
 *
 * @author Wolfram Huesken <wolfram.huesken@zalora.com>
 */
@Slf4j
public class DefaultInfiniBridge extends AbstractInfiniBridge {

    private IgniteCache<String, MemcachedItem> igniteCache;

    public DefaultInfiniBridge(IgniteCache<String, MemcachedItem> igniteCache) {
        super(igniteCache);
        this.igniteCache = igniteCache;
    }

    @Override
    public LocalCacheElement get(String key) {
        MemcachedItem item = igniteCache.get(key);
        if (item == null) {
            return null;
        }

        return createLocalCacheElement(item);
    }

    @Override
    public Collection<LocalCacheElement> getMulti(Set<String> keys) {
        return igniteCache.getAll(keys).entrySet().stream()
            .map(entry -> createLocalCacheElement(entry.getValue())).collect(Collectors.toList());
    }

    @Override
    public void put(String key, LocalCacheElement localCacheElement) {
        MemcachedItem memcachedItem = createMemcachedItem(localCacheElement);
        igniteCache.put(key, memcachedItem);
    }

    @Override
    public boolean replace(String key, LocalCacheElement localCacheElement) {
        MemcachedItem memcachedItem = createMemcachedItem(localCacheElement);
        return igniteCache.replace(key, memcachedItem);
    }

    @Override
    public boolean replace(String key, LocalCacheElement oldItem, LocalCacheElement newItem) {
        MemcachedItem oldMemcachedItem = createMemcachedItem(oldItem);
        MemcachedItem newMemcachedItem = createMemcachedItem(newItem);

        return igniteCache.replace(key, oldMemcachedItem, newMemcachedItem);
    }

    @Override
    public boolean touch(String key) {
        MemcachedItem item = igniteCache.get(key);
        return item != null && igniteCache.replace(key, item);
    }

    @Override
    public boolean putIfAbsent(String key, LocalCacheElement localCacheElement) {
        MemcachedItem memcachedItem = createMemcachedItem(localCacheElement);
        return igniteCache.putIfAbsent(key, memcachedItem);
    }

    @Override
    public Integer crement(String key, int modifier) {
        MemcachedItem item = igniteCache.get(key);
        if (item.getFlags() != 1) {
            return null;
        }

        int old = Integer.parseInt(new String(item.getData()));

        MemcachedItem oldItem = new MemcachedItem(item);
        item.setData(String.format("%d", old + modifier).getBytes());

        return igniteCache.replace(key, oldItem, item) ? old + modifier : null;
    }

    private LocalCacheElement createLocalCacheElement(MemcachedItem memcachedItem) {
        LocalCacheElement localCacheElement = new LocalCacheElement(
            memcachedItem.getKey(), memcachedItem.getFlags(), memcachedItem.getExpire(), 0
        );

        localCacheElement.setData(ChannelBuffers.copiedBuffer(memcachedItem.getData()));
        return localCacheElement;
    }

    private MemcachedItem createMemcachedItem(LocalCacheElement lce) {
        byte[] data = new byte[lce.getData().capacity()];
        lce.getData().getBytes(0, data);
        return new MemcachedItem(lce.getKey(), data, lce.getFlags(), lce.getExpire());
    }

}
