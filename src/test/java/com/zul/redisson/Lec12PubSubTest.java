package com.zul.redisson;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.redisson.api.RPatternTopicReactive;
import org.redisson.api.RTopicReactive;
import org.redisson.api.listener.PatternMessageListener;
import org.redisson.client.codec.StringCodec;

import reactor.core.publisher.Flux;

public class Lec12PubSubTest extends BaseTest {

    @Test
    public void subscriber1() {
        RTopicReactive topic = this.client.getTopic("slack-room1", StringCodec.INSTANCE);
        topic.getMessages(String.class)
                .doOnError(System.out::println)
                .doOnNext(System.out::println)
                .subscribe();
        sleep(600000);
    }

    @Test
    public void subscriber2() {
        RPatternTopicReactive patternTopic = this.client.getPatternTopic("slack-room*", StringCodec.INSTANCE);
        patternTopic.addListener(String.class, new PatternMessageListener<String>() {
            @Override
            public void onMessage(CharSequence pattern, CharSequence topic, String msg) {
                System.out.println(pattern + " : " + topic + " - " + msg);
            }
        }).subscribe();
        sleep(600000);
    }

    @Test
    public void producer() {
        RTopicReactive topic = this.client.getTopic("slack-room1", StringCodec.INSTANCE);
        Flux.interval(Duration.ofSeconds(1))
                .map(i -> "Msg: " + i)
                .doOnNext(System.out::println)
                .flatMap(msg -> topic.publish(msg))
                .subscribe();
        sleep(600000);
    }

}
