package com.ssafy.enjoytrip.config;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class RedisTestConfig {

    private static final GenericContainer<?> REDIS_CONTAINER;

    static {
        REDIS_CONTAINER =
            new GenericContainer<>(DockerImageName.parse("redis:latest"))
                .withExposedPorts(6379);

    }

    @BeforeAll
    static void beforeAll() {
        REDIS_CONTAINER.start();
        System.setProperty("spring.redis.host", REDIS_CONTAINER.getHost());
        System.setProperty("spring.redis.port",
            String.valueOf(REDIS_CONTAINER.getFirstMappedPort()));
    }

    @AfterAll
    static void afterAll() {
        REDIS_CONTAINER.stop();
    }
}
