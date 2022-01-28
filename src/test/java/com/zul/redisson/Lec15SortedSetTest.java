package com.zul.redisson;

import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.client.codec.StringCodec;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec15SortedSetTest extends BaseTest {

    @Test
    public void sortedSet() {
        RScoredSortedSetReactive<String> sortedSet = client.getScoredSortedSet("student:score", StringCodec.INSTANCE);
        Mono<Void> clear = sortedSet.add(1, "sam")
                .then(sortedSet.add(1, "mike"))
                .then(sortedSet.add(1, "jake"))
                .then();
        StepVerifier.create(clear.then(clear)).verifyComplete();
        Mono<Void> mono = sortedSet.addScore("sam", 12.25)
                .then(sortedSet.add(23.25, "mike"))
                .then(sortedSet.add(7, "jake"))
                .then();
        StepVerifier.create(clear.then(mono)).verifyComplete();
        sortedSet.entryRange(0, 1)
                .flatMapIterable(Function.identity())
                .map(se -> se.getScore() + " : " + se.getValue())
                .doOnNext(val -> System.out.println("Sorted set: " + val))
                .subscribe();
        sleep(1000);
    }

}
