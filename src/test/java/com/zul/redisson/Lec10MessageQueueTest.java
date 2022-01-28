package com.zul.redisson;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBlockingDequeReactive;
import org.redisson.client.codec.LongCodec;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec10MessageQueueTest extends BaseTest {

    private RBlockingDequeReactive<Object> msgQueue;

    @BeforeEach
    public void setupQueue() {
        msgQueue = client.getBlockingDeque("message-queue", LongCodec.INSTANCE);
    }

    @Test
    public void consumer1() {
        msgQueue.takeElements()
                .doOnNext(i -> System.out.println("Consumer 1: " + i))
                .doOnError(System.out::println)
                .subscribe();
        sleep(600000);
    }

    @Test
    public void consumer2() {
        msgQueue.takeElements()
                .doOnNext(i -> System.out.println("Consumer 2: " + i))
                .doOnError(System.out::println)
                .subscribe();
        sleep(600000);
    }

    @Test
    public void producer() {
        Mono<Void> mono = Flux.range(1, 100)
                .delayElements(Duration.ofMillis(500))
                .doOnNext(i -> System.out.println("going to add: " + i))
                .flatMap(i -> msgQueue.add(i))
                .then();
        StepVerifier.create(mono).verifyComplete();
    }

}