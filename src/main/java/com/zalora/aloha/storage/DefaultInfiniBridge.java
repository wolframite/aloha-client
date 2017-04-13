package com.zalora.aloha.storage;

import com.zalora.aloha.memcached.MemcachedItem;
import com.zalora.jmemcached.LocalCacheElement;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.RemoteCache;
import org.jboss.netty.buffer.ChannelBuffers;

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

        MetadataValue<MemcachedItem> ce = ispanCache.getWithMetadata(localKey);
        if (ce == null || ce.getValue() == null) {
            return null;
        }

        return getLocalCacheElement(ce.getValue());
    }

    @Override
    public Collection<LocalCacheElement> getMulti(Set<String> keys) {
        return ispanCache.getAll(keys).entrySet().stream()
            .map(entry -> getLocalCacheElement(entry.getValue()))
            .collect(Collectors.toList());
    }

    @Override
    public LocalCacheElement put(String key, LocalCacheElement value) {
        ispanCache.put(key, getMemcachedItem(value));
        return null;
    }

    @Override
    public boolean remove(Object key, Object localCacheElement) {
        return ispanCache.remove(key, getMemcachedItem((LocalCacheElement) localCacheElement));
    }

    @Override
    public boolean replace(String key, LocalCacheElement searchElement, LocalCacheElement replaceElement) {
        return ispanCache.replace(key, getMemcachedItem(searchElement), getMemcachedItem(replaceElement));
    }

    @Override
    public LocalCacheElement replace(String key, LocalCacheElement localCacheElement) {
        ispanCache.replace(key, getMemcachedItem(localCacheElement));
        return null;
    }

    @Override
    public LocalCacheElement putIfAbsent(String key, LocalCacheElement value) {
        ispanCache.putIfAbsent(key, getMemcachedItem(value));
        return null;
    }

    private LocalCacheElement getLocalCacheElement(MemcachedItem memcachedItem) {
        LocalCacheElement localCacheElement =  new LocalCacheElement(
            memcachedItem.getKey(), memcachedItem.getFlags(), memcachedItem.getExpire(), 0
        );
        localCacheElement.setData(ChannelBuffers.copiedBuffer(memcachedItem.getData()));

        return localCacheElement;
    }

    private MemcachedItem getMemcachedItem(LocalCacheElement lce) {
        byte[] data = new byte[lce.getData().capacity()];
        lce.getData().getBytes(0, data);

        return new MemcachedItem(data, lce.getExpire(), lce.getFlags(), lce.getKey());
    }

}
