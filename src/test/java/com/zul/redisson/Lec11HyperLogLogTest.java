package com.zul.redisson;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.redisson.api.RHyperLogLogReactive;
import org.redisson.client.codec.LongCodec;

import reactor.test.StepVerifier;

public class Lec11HyperLogLogTest extends BaseTest {

    @Test
    public void count() {
        RHyperLogLogReactive<Long> counter = client.getHyperLogLog("user:visits", LongCodec.INSTANCE);
        List<Long> longList = LongStream.rangeClosed(1, 25)
                .boxed()
                .collect(Collectors.toList());

        StepVerifier.create(counter.addAll(longList).then()).verifyComplete();

        counter.count()
                .doOnNext(System.out::println)
                .subscribe();
    }

}
