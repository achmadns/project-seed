package com.github.achmadns.lab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.testng.internal.thread.CountDownAdapter;
import reactor.Environment;
import reactor.fn.timer.Timer;
import reactor.rx.broadcast.Broadcaster;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static reactor.Environment.get;

public class BroadcasterExperiment {
    private static final Logger log = LoggerFactory.getLogger(BroadcasterExperiment.class);
    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    static {
        Environment.initializeIfEmpty().
                assignErrorJournal(t -> log.error("Ooops! ", t));
    }

    @Test(timeOut = 3000)
    public void notify_from_simple_thread_should_ok() throws InterruptedException {
        final Broadcaster<Long> stream = Broadcaster.<Long>create(get());
        final CountDownAdapter waiter = new CountDownAdapter(2);
        stream.consume(aLong -> {
            waiter.countDown();
        });
        stream.consume(aLong -> log.info("Notified at {}", new Date(aLong)));
        new Thread(() -> {
            try {
                sleep(1000);
                stream.onNext(System.currentTimeMillis());
                sleep(1000);
                stream.onNext(System.currentTimeMillis());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        waiter.await();
    }

    @Test(timeOut = 3000)
    public void notify_using_executor_should_ok() throws InterruptedException {
        final Broadcaster<Object> stream = Broadcaster.create(get());
        final CountDownAdapter waiter = new CountDownAdapter(1);
        stream.consume(o -> waiter.countDown());
        executor.execute(() -> {
            try {
                sleep(1000);
                stream.onNext("");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        waiter.await();
    }

    @Test(timeOut = 3000)
    public void notify_from_timer_thread_should_ok() throws InterruptedException {
        final Broadcaster<Object> stream = Broadcaster.create(get());
        final CountDownAdapter waiter = new CountDownAdapter(1);
        stream.consume(o -> waiter.countDown());
        final Timer timer = Environment.get().getTimer();
        timer.schedule(
                aLong -> stream.onNext(""),
                1, TimeUnit.SECONDS, 0);
        waiter.await();
    }

}
