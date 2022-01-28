package com.zul.redisson;

import java.util.Arrays;
import java.util.Map;

import com.zul.redisson.dto.Student;

import org.junit.jupiter.api.Test;
import org.redisson.api.RMapReactive;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.TypedJsonJacksonCodec;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec06MapTest extends BaseTest {

    @Test
    public void mapTest() {
        RMapReactive<Object, Object> map = client.getMap("user:1", StringCodec.INSTANCE);
        Mono<Object> name = map.put("name", "Zul");
        Mono<Object> age = map.put("age", "20");
        Mono<Object> city = map.put("city", "Seoul");
        StepVerifier.create(name.concatWith(age).concatWith(city)).verifyComplete();
    }

    @Test
    public void mapTest2() {
        RMapReactive<Object, Object> map = client.getMap("user:1", StringCodec.INSTANCE);
        Map<String, String> javaMap = Map.of(
                "name", "Jake",
                "age", "30",
                "city", "Miami");
        StepVerifier.create(map.putAll(javaMap)).verifyComplete();
    }

    @Test
    public void mapTest3() {
        // Map<Integer, Student>
        TypedJsonJacksonCodec codec = new TypedJsonJacksonCodec(Integer.class, Student.class);
        RMapReactive<Integer, Student> map = client.getMap("users", codec);

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

        Mono<Student> mono1 = map.put(1, student1);
        Mono<Student> mono2 = map.put(2, student2);

        StepVerifier.create(mono1.concatWith(mono2).then())
                .verifyComplete();

    }

}
