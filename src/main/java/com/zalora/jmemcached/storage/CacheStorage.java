package com.zalora.jmemcached.storage;

import java.util.*;

/**
 * Removed the extension of the Map interface, because the methods don't fit ignite's cache
 * Added the useful map methods to the interface
 *
 * @author Ryan Daum
 * @author Wolfram Huesken <wolfram.huesken@zalora.com>
 */
public interface CacheStorage<K, V> {

    /**
     * @return the capacity (in # of items) of the storage
     */
    int size();

    /**
     * Remove all items from the cache synchronously
     */
    void clear();

    boolean containsKey(K key);

    boolean remove(K key);

    V get(K key);

    void put(K key, V value);

    boolean replace(K key, V value);

    boolean replace(K key, V localCacheElement, V localCacheElement2);

    boolean putIfAbsent(K key, V value);

    /**
     * increment/decrement key
     * @param key
     * @param modifier
     * @return
     */
    Integer crement(K key, int modifier);

    /**
     * Try to improve performance with a multi-get
     */
    Collection<V> getMulti(Set<K> keys);

    /**
     * Reset expiration
     */
    boolean touch(K key);

    /**
     * Close the local cache, the cluster data will remain
     */
    void close();

}
