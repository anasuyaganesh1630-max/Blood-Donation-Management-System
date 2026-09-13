package com.blooddonation.backend.config;

import org.slf.Logger;
import org.slf.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvDebugRunner {

    private static final Logger log = LoggerFactory.getLogger(EnvDebugRunner.class);

    public EnvDebugRunner(
            @Value("${MYSQL_HOST:NOT_SET}") String host,
            @Value("${MYSQL_PORT:NOT_SET}") String port,
            @Value("${MYSQL_DATABASE:NOT_SET}") String db,
            @Value("${MYSQL_USER:NOT_SET}") String user) {

        log.info("================ ENV VAR DEBUG ================");
        log.info("MYSQL_HOST    : {}", host);
        log.info("MYSQL_PORT    : {}", port);
        log.info("MYSQL_DATABASE: {}", db);
        log.info("MYSQL_USER    : {}", user);
        log.info("===============================================");
    }
}