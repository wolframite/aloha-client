package com.zalora.aloha.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;

/**
 * @author Wolfram Huesken <wolfram.huesken@zalora.com>
 */
@Component
public class MemcachedConfig {

    @Value("${memcached.host}")
    private String host;

    @Value("${memcached.port}")
    private int primaryPort;

    @Getter
    @Value("${memcached.idleTime}")
    private int idleTime;

    @Getter
    @Value("${memcached.verbose}")
    private boolean verbose;

    @Bean
    public InetSocketAddress socketAddress() {
        return new InetSocketAddress(host, primaryPort);
    }

}
