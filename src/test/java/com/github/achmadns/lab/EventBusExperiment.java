package com.github.achmadns.lab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.thread.CountDownAdapter;
import reactor.Environment;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.bus.selector.Selector;
import reactor.fn.timer.Timer;
import reactor.rx.Promise;
import reactor.rx.Promises;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static java.util.regex.Pattern.compile;
import static org.assertj.core.api.Assertions.assertThat;
import static reactor.Environment.get;
import static reactor.bus.EventBus.create;
import static reactor.bus.selector.Selectors.$;
import static reactor.bus.selector.Selectors.R;

public class EventBusExperiment {
    private static final Logger log = LoggerFactory.getLogger(EventBusExperiment.class);
    public static final String FINISHED = "finished";
    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    static {
        Environment.initializeIfEmpty().
                assignErrorJournal(t -> log.error("Ooops! ", t));
    }

    @Test(timeOut = 3000)
    public void notify_from_simple_thread_should_ok() throws InterruptedException {
        final EventBus bus = create(get());
        final CountDownAdapter waiter = new CountDownAdapter(1);
        final Selector<String> finished = $(FINISHED);
        bus.on(finished, event -> waiter.countDown());
        new Thread(() -> {
            try {
                sleep(1000);
                bus.notify(FINISHED);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        waiter.await();
    }

    @Test(timeOut = 3000)
    public void notify_from_simple_thread_using_send_and_receive_should_ok() throws InterruptedException {
        final EventBus bus = EventBus.config().env(get()).dispatcher(Environment.SHARED).get();
        final CountDownAdapter waiter = new CountDownAdapter(1);
        final Selector<String> finished = $(FINISHED);
        bus.receive(finished, event -> {
            waiter.countDown();
            return null;
        });
        new Thread(() -> {
            try {
                sleep(1000);
                bus.send(FINISHED, Event.wrap("").setReplyTo(FINISHED));
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
        final Selector<String> finished = $(FINISHED);
        bus.on(finished, event -> waiter.countDown());
        executor.execute(() -> {
            try {
                sleep(1000);
                bus.notify(FINISHED);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        waiter.await();
    }


    @Test(timeOut = 3000)
    public void notify_from_timer_thread_should_ok() throws InterruptedException {
        final Selector<String> finished = $(FINISHED);
        final EventBus bus = create(get());
        final CountDownAdapter waiter = new CountDownAdapter(1);
        bus.on(finished,
                event -> waiter.countDown()
        );
        final Timer timer = get().getTimer();
        timer.schedule(
                aLong -> bus.notify(FINISHED),
                1, TimeUnit.SECONDS, 0);
        waiter.await();
    }

    /**
     * May be Promise is not suitable for notification system.
     *
     * @throws InterruptedException
     */
    @Test(timeOut = 3000, enabled = false)
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

    /**
     * May be Promise is not suitable for notification system.
     *
     * @throws InterruptedException
     */
    @Test(timeOut = 3000, enabled = false)
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

    @Test(timeOut = 3000)
    public void regex_should_ok() throws InterruptedException {
        final CountDownLatch waiter = new CountDownLatch(1);
        final EventBus bus = create(get());
        bus.on(R("(#SET.1)(.+)"), event -> waiter.countDown());
        bus.notify("#SET.1 bla bla bla");
        waiter.await();
    }

    @DataProvider(name = "regex-input")
    public Object[][] provideRegex() {
        return new Object[][]{
                new Object[]{"(#SET.1)(.+)", "#SET.1 bla bla bla", true},
                new Object[]{"(#SET\\.1)(.+)", "#SET.1 bla bla bla", true},
                new Object[]{"(#SET.1)(.+)", "#set.1 bla bla bla", false},
                new Object[]{"((?i)#SET.1)(.+)", "#set.1 bla bla bla", true},
                new Object[]{"((?i)#SeT.1)(.+)", "#set.1 bla bla bla", true},
                new Object[]{"^[A-Z0-9]+$", "ABC1", true},
                new Object[]{"^[A-Z0-9]+$", "ABC1=+", false},
                new Object[]{"^[A-Z0-9]+$", "abc1", false},
                new Object[]{"(?i)^[A-Z0-9]+$", "abc1", true}
        };
    }

    @Test(dataProvider = "regex-input")
    public void regex(String pattern, String input, boolean matches) {
        assertThat(matches).isEqualTo(compile(pattern).matcher(input).matches());
    }
}
