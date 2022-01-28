package com.zul.redisson.config;

import java.util.Objects;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Lazy
@TestConfiguration
public class RedissonConfig {

    private RedissonClient redissonClient;

    public RedissonClient getClient() {
        if (Objects.isNull(redissonClient)) {
            Config config = new Config();
            config.useSingleServer().setAddress("redis://127.0.0.1:6379");
            redissonClient = Redisson.create(config);
        }
        return redissonClient;
    }

    @Bean
    public RedissonReactiveClient getReativeClient() {
        return getClient().reactive();
    }

}