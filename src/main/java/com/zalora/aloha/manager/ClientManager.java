package com.zalora.aloha.manager;

import javax.annotation.PostConstruct;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Wolfram Huesken <wolfram.huesken@zalora.com>
 */
@Component
public class ClientManager {

    @Autowired
    private IgniteConfiguration igniteConfiguration;

    private Ignite ignite;

    @PostConstruct
    public void init() {
        ignite = Ignition.start(igniteConfiguration);
        ignite.active(true);
    }

    @Bean
    public Ignite ignite() {
        return ignite;
    }

}
