package com.zul.redisson;

import java.time.Duration;

import com.zul.redisson.assignment.Category;
import com.zul.redisson.assignment.PriorityQueue;
import com.zul.redisson.assignment.UserOrder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.codec.TypedJsonJacksonCodec;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec16PriorityQueueTest extends BaseTest {

    private PriorityQueue priorityQueue;

    @BeforeEach
    public void setupQueue() {
        RScoredSortedSetReactive<UserOrder> sortedSet = client.getScoredSortedSet("user:order:queue",
                new TypedJsonJacksonCodec(UserOrder.class));
        priorityQueue = new PriorityQueue(sortedSet);
    }

    @Test
    public void producer() {
        Flux.interval(Duration.ofSeconds(1))
                .map(l -> (l.intValue() * 5))
                .doOnNext(i -> {
                    UserOrder u1 = new UserOrder(i + 1, Category.GUEST);
                    UserOrder u2 = new UserOrder(i + 2, Category.STD);
                    UserOrder u3 = new UserOrder(i + 3, Category.PRIME);
                    UserOrder u4 = new UserOrder(i + 4, Category.STD);
                    UserOrder u5 = new UserOrder(i + 5, Category.GUEST);
                    Mono<Void> mono = Flux.just(u1, u2, u3, u4, u5)
                            .flatMap(priorityQueue::add)
                            .then();
                    StepVerifier.create(mono).verifyComplete();
                }).subscribe();
        sleep(60000);
    }

    @Test
    public void consumer() {
        this.priorityQueue.takeItems()
                .delayElements(Duration.ofMillis(500))
                .doOnNext(i -> System.out.println("Order: " + i))
                .subscribe();
        sleep(600000);
    }

}
