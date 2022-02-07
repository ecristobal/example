package com.example.enrich.spring;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

@TestConfiguration
public class EmbeddedRedisTestConfiguration {

    private final RedisServer redisServer;

    public EmbeddedRedisTestConfiguration(final @Value("${spring.redis.port}") int redisPort) {
        this.redisServer = new RedisServer(redisPort);
    }

    @PostConstruct
    void setUp() {
        this.redisServer.start();
    }

    @PreDestroy
    void tearDown() {
        this.redisServer.stop();
    }

}
