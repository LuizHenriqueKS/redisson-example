package com.zul.redisson;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLongReactive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec03NumberTest extends BaseTest {

    @Test
    public void keyValueIncreaseTest() {
        // set k v -- incr, decr
        RAtomicLongReactive atomicLong = client.getAtomicLong("user:1:visit");
        atomicLong.set(0);
        Mono<Void> mono = Flux.range(1, 30)
                .delayElements(Duration.ofSeconds(1))
                .flatMap(mapper -> atomicLong.incrementAndGet())
                .doOnNext(val -> System.out.println("incrementAndGet: " + val))
                .then();
        StepVerifier.create(mono).verifyComplete();
    }

}