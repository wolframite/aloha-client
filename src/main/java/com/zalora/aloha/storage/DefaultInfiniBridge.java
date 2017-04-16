package com.zalora.aloha.storage;

import com.zalora.aloha.memcached.MemcachedItem;
import com.zalora.jmemcached.LocalCacheElement;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.RemoteCache;
import org.jboss.netty.buffer.ChannelBuffers;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Hook up jMemcached and Infinispan
 *
 * @author Wolfram Huesken <wolfram.huesken@zalora.com>
 */
@Slf4j
public class DefaultInfiniBridge extends AbstractInfiniBridge {

    private RemoteCache<String, MemcachedItem> ispanCache;

    public DefaultInfiniBridge(RemoteCache<String, MemcachedItem> ispanCache) {
        super(ispanCache);
        this.ispanCache = ispanCache;
    }

    @Override
    public LocalCacheElement get(Object key) {
        final String localKey = (String) key;

        MetadataValue<MemcachedItem> metadataValue = ispanCache.getWithMetadata(localKey);
        if (metadataValue == null || metadataValue.getValue() == null) {
            return null;
        }

        return createLocalCacheElement(metadataValue.getValue());
    }

    @Override
    public Collection<LocalCacheElement> getMulti(Set<String> keys) {
        return ispanCache.getAll(keys).entrySet().stream()
            .map(entry -> createLocalCacheElement(entry.getValue()))
            .collect(Collectors.toList());
    }

    @Override
    public LocalCacheElement put(String key, LocalCacheElement localCacheElement) {
        ispanCache.put(
            key, createMemcachedItem(localCacheElement), localCacheElement.getExpire(), TimeUnit.MILLISECONDS
        );

        return null;
    }

    @Override
    public boolean remove(Object key, Object localCacheElement) {
        return ispanCache.remove(key, createMemcachedItem((LocalCacheElement) localCacheElement));
    }

    @Override
    public boolean replace(String key, LocalCacheElement searchElement, LocalCacheElement replaceElement) {
        return ispanCache.replace(
            key, createMemcachedItem(searchElement), createMemcachedItem(replaceElement),
            replaceElement.getExpire(), TimeUnit.MILLISECONDS
        );
    }

    @Override
    public LocalCacheElement replace(String key, LocalCacheElement localCacheElement) {
        ispanCache.replace(
            key, createMemcachedItem(localCacheElement), localCacheElement.getExpire(), TimeUnit.MILLISECONDS
        );

        return null;
    }

    @Override
    public LocalCacheElement putIfAbsent(String key, LocalCacheElement localCacheElement) {
        ispanCache.putIfAbsent(
            key, createMemcachedItem(localCacheElement), localCacheElement.getExpire(), TimeUnit.MILLISECONDS
        );

        return null;
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

        return new MemcachedItem(data, lce.getExpire(), lce.getFlags(), lce.getKey());
    }

}
