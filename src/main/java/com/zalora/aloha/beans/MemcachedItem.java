package com.zalora.aloha.beans;

import lombok.*;
import java.io.Serializable;

/**
 * @author Wolfram Huesken <wolfram.huesken@zalora.com>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemcachedItem implements Serializable {

    private static final long serialVersionUID = 7503234879985469265L;

    private String key;
    private byte[] data;
    private long flags;
    private long expire;

    public MemcachedItem(String key) {
        this.key = key;
    }

    // Dolly constructor
    public MemcachedItem(MemcachedItem item) {
        this.key = item.getKey();
        this.data = item.getData();
        this.flags = item.getFlags();
        this.expire = item.getExpire();
    }

}
