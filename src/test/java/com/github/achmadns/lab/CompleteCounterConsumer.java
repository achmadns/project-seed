package com.github.achmadns.lab;

import org.testng.internal.thread.CountDownAdapter;
import reactor.fn.Consumer;
import reactor.rx.Promise;

/**
 * Simple consumer that has count down attached to it.
 *
 * @param <T>
 */
public class CompleteCounterConsumer<T> implements Consumer<Promise<T>> {
    private final CountDownAdapter counter;

    public CompleteCounterConsumer(int count) {
        this(new CountDownAdapter(count));
    }

    public CompleteCounterConsumer(CountDownAdapter counter) {
        this.counter = counter;
    }

    public void accept(Promise<T> tPromise) {
        counter.countDown();
    }


    public static <T> CompleteCounterConsumer<T> create() {
        return create(1);
    }

    public static <T> CompleteCounterConsumer<T> create(int count) {
        return new CompleteCounterConsumer<T>(count);
    }

    public void await() throws InterruptedException {
        counter.await();
    }

    public void await(long timeout) throws InterruptedException {
        counter.await(timeout);
    }
}