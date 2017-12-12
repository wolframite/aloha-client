package com.zalora.jmemcached;

import java.util.*;

/**
 * @author Ryan Daum
 */
public interface Cache<E extends CacheElement> {

    /**
     * Handle the deletion of an item from the cache.
     *
     * @param key  the key for the item
     * @return the message response
     */
    DeleteResponse delete(String key);

    /**
     * Add an element to the cache
     *
     * @param e the element to add
     * @return the store response code
     */
    StoreResponse add(E e);

    /**
     * Replace an element in the cache
     *
     * @param e the element to replace
     * @return the store response code
     */
    StoreResponse replace(E e);

    /**
     * Append bytes to the end of an element in the cache
     *
     * @param element the element to append
     * @return the store response code
     */
    StoreResponse append(E element);

    /**
     * Prepend bytes to the end of an element in the cache
     *
     * @param element the element to append
     * @return the store response code
     */
    StoreResponse prepend(E element);

    /**
     * Set an element in the cache
     *
     * @param e the element to set
     * @return the store response code
     */
    StoreResponse set(E e);

    /**
     * Set an element in the cache but only if the element has not been touched
     * since the last 'gets'
     *
     * @param cas_key the cas key returned by the last gets
     * @param e       the element to set
     * @return the store response code
     */
    StoreResponse cas(Long cas_key, E e);

    /**
     * Increment/decrement an (integer) element in the cache
     *
     * @param key the key to increment
     * @param mod the amount to add to the value
     * @return the message response
     */
    Integer get_add(String key, int mod);

    /**
     * Touching an existing item changes the expiration to the given value
     * @param key the key to touch
     * @param expire expiration in milliseconds
     * @return Touch reponse
     */
    TouchResponse touch(String key, long expire);

    /**
     * Get element(s) from the cache
     *
     * @param keys the key for the element to lookup
     * @return the element, or 'null' in case of cache miss.
     */
    E[] get(String... keys);

    /**
     * Flush all cache entries
     *
     * @return command response
     */
    boolean flush_all();

    /**
     * Flush all cache entries with a timestamp after a given expiration time
     *
     * @param expire the flush time in seconds
     * @return command response
     */
    boolean flush_all(int expire);

    /**
     * Close the cache, freeing all resources on which it depends.
     */
    void close();

    /**
     * @return the # of items in the cache
     */
    long getCurrentItems();

    /**
     * @return the maximum size of the cache (in bytes)
     */
    long getLimitMaxBytes();

    /**
     * @return the current cache usage (in bytes)
     */
    long getCurrentBytes();

    /**
     * @return the number of get commands executed
     */
    int getGetCmds();

    /**
     * @return the number of set commands executed
     */
    int getSetCmds();

    /**
     * @return the number of get hits
     */
    int getGetHits();

    /**
     * @return the number of stats
     */
    int getGetMisses();

    /**
     * Retrieve stats about the cache. If an argument is specified, a specific category of stats is requested.
     *
     * @param arg a specific extended stat sub-category
     * @return a map of stats
     */
    Map<String, Set<String>> stat(String arg);

    /**
     * Enum defining response statuses from set/add type commands
     */
    enum StoreResponse {
        STORED, NOT_STORED, EXISTS, NOT_FOUND
    }

    /**
     * Enum defining responses statuses from removal commands
     */
    enum DeleteResponse {
        DELETED, NOT_FOUND
    }

    /**
     * Response for touch command
     */
    enum TouchResponse {
        TOUCHED, NOT_FOUND
    }
}
