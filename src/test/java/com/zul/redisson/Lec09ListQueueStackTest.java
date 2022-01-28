package com.zul.redisson;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.redisson.api.RDequeReactive;
import org.redisson.api.RListReactive;
import org.redisson.api.RQueueReactive;
import org.redisson.client.codec.LongCodec;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec09ListQueueStackTest extends BaseTest {

    @Test
    public void listTest() {
        // lrange number-input 0 - 1
        RListReactive<Object> list = client.getList("number-input", LongCodec.INSTANCE);
        Mono<Void> listClear = list.delete().then();
        Mono<Void> listAdd = Flux.range(1, 10)
                .map(Long::valueOf)
                .flatMap(list::add)
                .then();
        // StepVerifier.create(listAdd.concatWith(listClear)).verifyComplete();
        StepVerifier.create(listClear.concatWith(listAdd)).verifyComplete();
        StepVerifier.create(list.size()).expectNext(10).verifyComplete();
    }

    @Test
    public void listTest2() {
        // lrange number-input 0 - 1
        RListReactive<Object> list = client.getList("number-input", LongCodec.INSTANCE);

        List<Long> longList = LongStream.rangeClosed(1, 10)
                .boxed()
                .collect(Collectors.toList());

        Mono<Void> listClear = list.delete().then();
        Mono<Void> listAddAll = list.addAll(longList).then();

        StepVerifier.create(listClear.concatWith(listAddAll)).verifyComplete();
        StepVerifier.create(list.size()).expectNext(10).verifyComplete();
    }

    @Test
    public void queueTest() {
        // lrange number-input 0 - 1
        RQueueReactive<Long> queue = client.getQueue("number-input", LongCodec.INSTANCE);

        List<Long> longList = LongStream.rangeClosed(1, 10)
                .boxed()
                .collect(Collectors.toList());

        Mono<Void> queueClear = queue.delete().then();
        Mono<Void> queueAddAll = queue.addAll(longList).then();
        Mono<Void> queuePoll = queue.poll()
                .repeat(3)
                .doOnNext(System.out::println)
                .then();
        StepVerifier.create(queueClear.concatWith(queueAddAll).concatWith(queuePoll)).verifyComplete();
        StepVerifier.create(queue.size()).expectNext(6).verifyComplete();
    }

    @Test
    public void stackTest() { // Deque
        RDequeReactive<Object> deck = client.getDeque("number-input", LongCodec.INSTANCE);

        List<Long> longList = LongStream.rangeClosed(1, 10)
                .boxed()
                .collect(Collectors.toList());

        Mono<Void> deckClear = deck.delete().then();
        Mono<Void> deckAddAll = deck.addAll(longList).then();
        Mono<Void> deckPollLast = deck.pollLast()
                .repeat(3)
                .doOnNext(System.out::println)
                .then();
        StepVerifier.create(deckClear.concatWith(deckAddAll).concatWith(deckPollLast)).verifyComplete();
        StepVerifier.create(deck.size()).expectNext(6).verifyComplete();
    }

}
