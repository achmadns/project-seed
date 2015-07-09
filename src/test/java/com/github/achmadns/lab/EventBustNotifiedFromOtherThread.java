package com.github.achmadns.lab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.testng.internal.thread.CountDownAdapter;
import reactor.Environment;
import reactor.bus.EventBus;
import reactor.bus.selector.Selector;
import reactor.fn.timer.Timer;
import reactor.rx.Promise;
import reactor.rx.Promises;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static reactor.Environment.get;
import static reactor.bus.EventBus.create;
import static reactor.bus.selector.Selectors.$;

public class EventBustNotifiedFromOtherThread {
    private static final Logger log = LoggerFactory.getLogger(EventBustNotifiedFromOtherThread.class);
    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    static {
        Environment.initializeIfEmpty().
                assignErrorJournal(t -> log.error("Ooops! ", t));
    }

    @Test(timeOut = 3000)
    public void notify_from_simple_thread_should_ok() throws InterruptedException {
        final EventBus bus = create(get());
        final CountDownAdapter waiter = new CountDownAdapter(1);
        final Selector<String> finished = $("finished");
        bus.on(finished, event -> waiter.countDown());
        new Thread(() -> {
            try {
                sleep(1000);
                bus.notify(finished);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        waiter.await();
    }

    @Test(timeOut = 3000)
    public void notify_using_executor_should_ok() throws InterruptedException {
        final EventBus bus = create(get());
        final CountDownAdapter waiter = new CountDownAdapter(1);
        final Selector<String> finished = $("finished");
        bus.on(finished, event -> waiter.countDown());
        executor.execute(() -> {
            try {
                sleep(1000);
                bus.notify(finished);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        waiter.await();
    }


    @Test(timeOut = 3000)
    public void notify_from_timer_thread_should_ok() throws InterruptedException {
        final Selector<String> finished = $("finished");
        final EventBus bus = create(get());
        final CountDownAdapter waiter = new CountDownAdapter(1);
        bus.on(finished,
                event -> waiter.countDown()
        );
        final Timer timer = Environment.get().getTimer();
        timer.schedule(
                aLong -> bus.notify(finished),
                1, TimeUnit.SECONDS, 0);
        waiter.await();
    }

    @Test(timeOut = 3000)
    public void notify_on_promise_success_using_simple_thread_should_ok() throws InterruptedException {
        final CompleteCounterConsumer<Long> waiter = CompleteCounterConsumer.<Long>create();
        final Promise<Long> promise = Promises.<Long>prepare(get()).onComplete(waiter);
        new Thread(() -> {
            try {
                sleep(1000);
                promise.onNext(System.currentTimeMillis());
            } catch (InterruptedException e) {
                promise.onError(e);
            }
        }).start();
        waiter.await();
    }

    @Test(timeOut = 3000)
    public void notify_on_promise_success_using_executor_should_ok() throws InterruptedException {
        final CompleteCounterConsumer<Long> waiter = CompleteCounterConsumer.<Long>create();
        final Promise<Long> promise = Promises.<Long>prepare(get()).onComplete(waiter);
        executor.execute(() -> {
            try {
                sleep(1000);
                promise.onNext(System.currentTimeMillis());

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        waiter.await();
    }
}
