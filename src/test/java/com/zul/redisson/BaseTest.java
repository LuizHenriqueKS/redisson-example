package com.zul.redisson;

import com.zul.redisson.config.RedissonConfig;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public abstract class BaseTest {

    protected RedissonReactiveClient client;

    @BeforeEach
    public void setClient() {
        client = new RedissonConfig().getReativeClient();
    }

    @AfterEach
    public void shutdown() {
        client.shutdown();
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
