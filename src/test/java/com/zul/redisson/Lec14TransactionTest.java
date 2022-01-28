package com.zul.redisson;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RTransactionReactive;
import org.redisson.api.TransactionOptions;
import org.redisson.client.codec.LongCodec;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec14TransactionTest extends BaseTest {

    private RBucketReactive<Long> user1Balance;
    private RBucketReactive<Long> user2Balance;

    @BeforeEach
    public void accountSetup() {
        user1Balance = this.client.getBucket("user1:balance", LongCodec.INSTANCE);
        user2Balance = this.client.getBucket("user2:balance", LongCodec.INSTANCE);
        Mono<Void> user1BalanceSet = user1Balance.set(100L).then();
        Mono<Void> user2BalanceSet = user2Balance.set(0L).then();
        StepVerifier.create(user1BalanceSet.then(user2BalanceSet)).verifyComplete();
    }

    @AfterEach
    public void accountBalanceState() {
        Mono<Void> mono = Flux.zip(user1Balance.get(), user2Balance.get())
                .doOnNext(t -> System.out.println("Balances: " + t))
                .then();
        StepVerifier.create(mono).verifyComplete();
    }

    // user:1:balance 100
    // user:2:balance 0
    @Test
    public void nonTransactionTest() {
        this.transfer(user1Balance, user2Balance, 50)
                .thenReturn(0)
                .map(i -> (5 / i)) // some error
                .subscribe();
        sleep(1000);
    }

    @Test
    public void transactionTest() {
        RTransactionReactive transaction = client.createTransaction(TransactionOptions.defaults());
        RBucketReactive<Long> user1Balance = transaction.getBucket("user:1:balance", LongCodec.INSTANCE);
        RBucketReactive<Long> user2Balance = transaction.getBucket("user:2:balance", LongCodec.INSTANCE);
        this.transfer(user1Balance, user2Balance, 50)
                .thenReturn(0)
                .map(i -> (5 / i)) // some error
                .then(transaction.commit())
                .onErrorResume(ex -> transaction.rollback())
                .subscribe();
        sleep(1000);
    }

    private Mono<Void> transfer(RBucketReactive<Long> fromAccount, RBucketReactive<Long> toAccount, int amount) {
        return Flux.zip(fromAccount.get(), toAccount.get()) // [b1, b2]
                .filter(t -> t.getT1() >= amount)
                .flatMap(t -> fromAccount.set(t.getT1() - amount).thenReturn(t))
                .flatMap(t -> toAccount.set(t.getT2() + amount))
                .then();
    }

}
