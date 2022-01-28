package com.zul.redisson;

import java.util.Arrays;

import com.zul.redisson.dto.Student;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.codec.TypedJsonJacksonCodec;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec02KeyValueObjectTest extends BaseTest {

    @Test
    public void keyValueObjectTest() {
        Student student = Student
                .builder()
                .name("Zul")
                .age(20)
                .city("Seoul")
                .marks(Arrays.asList(1, 2, 3))
                .build();
        RBucketReactive<Object> bucket = this.client.getBucket("student:1", new TypedJsonJacksonCodec(Student.class));
        Mono<Void> set = bucket.set(student);
        Mono<Void> get = bucket.get()
                .doOnNext(System.out::println)
                .then();
        StepVerifier.create(set.concatWith(get)).verifyComplete();
    }

}
