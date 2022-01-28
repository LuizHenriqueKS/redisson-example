package com.zul.redisson;

import org.junit.jupiter.api.Test;
import org.redisson.client.codec.StringCodec;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec04BucketAsMapTest extends BaseTest {

    // user:1:name
    // user:2:name
    // user:3:name
    @Test
    public void buacketsAsMap() {
        this.client.getBucket("user:1:name", StringCodec.INSTANCE).set("Zul").block();
        this.client.getBucket("user:2:name", StringCodec.INSTANCE).set("Maria").block();
        this.client.getBucket("user:3:name", StringCodec.INSTANCE).set("John").block();
        Mono<Void> mono = this.client.getBuckets(StringCodec.INSTANCE)
                .get("user:1:name", "user:2:name", "user:3:name")
                .doOnNext(System.out::println)
                .then();
        StepVerifier.create(mono).verifyComplete();
    }

}
