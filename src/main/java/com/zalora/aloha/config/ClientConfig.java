package com.zalora.aloha.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Wolfram Huesken <wolfram.huesken@zalora.com>
 */
@Slf4j
@Component
public class ClientConfig {

    @Value("${aloha.internal.igniteHome}")
    private String igniteHome;

    @Bean
    public IgniteConfiguration igniteConfiguration() {
        IgniteConfiguration config = new IgniteConfiguration();
        config.setClientMode(true);
        config.setIgniteHome(igniteHome);

        // Get the logger under control
        config.setGridLogger(new Slf4jLogger(LoggerFactory.getLogger(getClass())));

        return config;
    }

}
