package com.zalora.aloha.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.net.InetSocketAddress;

/**
 * @author Wolfram Huesken <wolfram.huesken@zalora.com>
 */
@Component
public class MemcachedConfig {

    @Value("${memcached.host}")
    private String host;

    @Value("${memcached.port}")
    private int port;

    @Getter
    @Value("${memcached.idleTime}")
    private int idleTime;

    @Getter
    @Value("${memcached.verbose}")
    private boolean verbose;

    @Bean
    public InetSocketAddress inetSocketAddress() {
        return new InetSocketAddress(host, port);
    }

}
