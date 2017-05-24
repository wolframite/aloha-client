package com.zalora.aloha.storage;

import com.zalora.aloha.compressor.Compressor;
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
    private Compressor compressor;

    public DefaultInfiniBridge(RemoteCache<String, MemcachedItem> ispanCache, Compressor compressor) {
        super(ispanCache);

        this.ispanCache = ispanCache;
        this.compressor = compressor;
    }

    @Override
    public LocalCacheElement get(Object key) {
        final String localKey = (String) key;

        MetadataValue<MemcachedItem> metadataValue = ispanCache.getWithMetadata(localKey);
        if (metadataValue == null || metadataValue.getValue() == null) {
            return null;
        }

        compressor.afterGet(metadataValue.getValue());
        return createLocalCacheElement(metadataValue.getValue());
    }

    @Override
    public Collection<LocalCacheElement> getMulti(Set<String> keys) {
        return ispanCache.getAll(keys).entrySet().stream()
            .map(entry -> {
                compressor.afterGet(entry.getValue());
                return createLocalCacheElement(entry.getValue());
            })
            .collect(Collectors.toList());
    }

    @Override
    public LocalCacheElement put(String key, LocalCacheElement localCacheElement) {
        MemcachedItem memcachedItem = createMemcachedItem(localCacheElement);
        compressor.beforePut(memcachedItem);

        ispanCache.put(key, memcachedItem, localCacheElement.getExpire(), TimeUnit.MILLISECONDS);
        return null;
    }

    @Override
    public boolean remove(Object key, Object localCacheElement) {
        return ispanCache.remove(key, createMemcachedItem((LocalCacheElement) localCacheElement));
    }

    @Override
    public LocalCacheElement replace(String key, LocalCacheElement localCacheElement) {
        MemcachedItem memcachedItem = createMemcachedItem(localCacheElement);
        compressor.beforePut(memcachedItem);

        ispanCache.replace(key, memcachedItem, localCacheElement.getExpire(), TimeUnit.MILLISECONDS);
        return null;
    }

    @Override
    public LocalCacheElement putIfAbsent(String key, LocalCacheElement localCacheElement) {
        MemcachedItem memcachedItem = createMemcachedItem(localCacheElement);
        compressor.beforePut(memcachedItem);

        ispanCache.putIfAbsent(key, memcachedItem, localCacheElement.getExpire(), TimeUnit.MILLISECONDS);
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

        return new MemcachedItem(lce.getKey(), data, lce.getFlags(), lce.getExpire());
    }

}
