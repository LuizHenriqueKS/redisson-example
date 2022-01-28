package com.zul.redisson;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.client.codec.StringCodec;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec01KeyVaueTest extends BaseTest {

    @Test
    public void keyValueAccessTest() {
        RBucketReactive<Object> bucket = client.getBucket("user:1:name", StringCodec.INSTANCE);
        Mono<Void> set = bucket.set("Zul");
        Mono<Void> get = bucket.get().doOnNext(System.out::println).then();
        StepVerifier.create(set.concatWith(get)).verifyComplete();
    }

    @Test
    public void keyValueExpiryTest() {
        RBucketReactive<Object> bucket = client.getBucket("user:1:name", StringCodec.INSTANCE);
        Mono<Void> set = bucket.set("Zul", 10, TimeUnit.SECONDS);
        Mono<Void> get = bucket.get().doOnNext(System.out::println).then();
        StepVerifier.create(set.concatWith(get)).verifyComplete();
        sleep(5000);
        Mono<Boolean> mono = bucket.expire(60, TimeUnit.SECONDS);
        StepVerifier.create(mono).expectNext(true).verifyComplete();
        Mono<Void> ttl = bucket.remainTimeToLive().doOnNext(System.out::println).then();
        StepVerifier.create(ttl).verifyComplete();
    }

}
