package com.zul.redisson;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.zul.redisson.dto.Student;

import org.junit.jupiter.api.Test;
import org.redisson.api.RMapCacheReactive;
import org.redisson.codec.TypedJsonJacksonCodec;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec07MapCacheTest extends BaseTest {

    @Test
    public void mapCacheTest() {
        // Map<Integer, Student>
        TypedJsonJacksonCodec codec = new TypedJsonJacksonCodec(Integer.class, Student.class);
        RMapCacheReactive<Integer, Student> mapCache = client.getMapCache("users:cache", codec);

        Student student1 = Student
                .builder()
                .name("Zul")
                .age(20)
                .city("Seoul")
                .marks(Arrays.asList(1, 2, 3))
                .build();
        Student student2 = Student
                .builder()
                .name("Maria")
                .age(20)
                .city("São Paulo")
                .marks(Arrays.asList(1, 2, 3))
                .build();

        Mono<Student> st1 = mapCache.put(1, student1, 5, TimeUnit.SECONDS);
        Mono<Student> mono2 = mapCache.put(2, student2, 10, TimeUnit.SECONDS);

        StepVerifier.create(st1.concatWith(mono2).then())
                .verifyComplete();

        sleep(3000);

        mapCache.get(1).doOnNext(System.out::println).subscribe();
        mapCache.get(2).doOnNext(System.out::println).subscribe();

        sleep(3000);

        mapCache.get(1).doOnNext(System.out::println).subscribe();
        mapCache.get(2).doOnNext(System.out::println).subscribe();

        sleep(3000);
    }

}
