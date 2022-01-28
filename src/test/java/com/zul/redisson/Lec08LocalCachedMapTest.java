package com.zul.redisson;

import java.time.Duration;
import java.util.Arrays;

import com.zul.redisson.config.RedissonConfig;
import com.zul.redisson.dto.Student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.LocalCachedMapOptions.ReconnectionStrategy;
import org.redisson.api.LocalCachedMapOptions.SyncStrategy;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Lec08LocalCachedMapTest extends BaseTest {

    private RLocalCachedMap<Integer, Student> studentsMap;

    @BeforeEach
    public void setupClient() {
        RedissonConfig config = new RedissonConfig();
        RedissonClient redissonClient = config.getClient();

        LocalCachedMapOptions<Integer, Student> mapOptions = LocalCachedMapOptions.<Integer, Student>defaults()
                .syncStrategy(SyncStrategy.UPDATE)
                .reconnectionStrategy(ReconnectionStrategy.NONE);

        studentsMap = redissonClient.getLocalCachedMap(
                "students",
                new TypedJsonJacksonCodec(Integer.class, Student.class),
                mapOptions);
    }

    @Test
    public void reactiveTest1() {
        Mono.delay(Duration.ofSeconds(1)).map(d -> {
            System.out.println(d);
            return d;
        }).subscribe(System.out::println);
        System.out.println("Sleeping...");
        sleep(5000);
    }

    @Test
    public void reactiveTest2() {
        Mono.delay(Duration.ofSeconds(1)).map(d -> {
            System.out.println(d);
            return d;
        }).doOnNext(onNext -> {
            System.out.println("doOnNext");
        }).block();
        System.out.println("Sleeping...");
        sleep(5000);
    }

    @Test
    public void appServer1() {
        Student student1 = new Student("Zul", 20, "Seoul", Arrays.asList(1, 2, 3));
        Student student2 = new Student("Maria", 20, "São Paulo", Arrays.asList(1, 2, 3));

        studentsMap.put(1, student1);
        studentsMap.put(2, student2);

        Flux.interval(Duration.ofSeconds(1))
                .doOnNext(i -> System.out.println(i + " ==> " + studentsMap.get(1)))
                .subscribe();

        sleep(60000);

    }

    @Test
    public void appServer2() {
        Student student1 = new Student("Zul-updated", 20, "Seoul", Arrays.asList(1, 2, 3));
        studentsMap.put(1, student1);
    }

}
