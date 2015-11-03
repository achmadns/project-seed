package com.github.achmadns.lab;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import ratpack.test.MainClassApplicationUnderTest;
import reactor.Environment;
import reactor.rx.Promise;
import reactor.rx.Promises;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static ratpack.test.http.TestHttpClient.testHttpClient;

/**
 * Created by achmad on 03/11/15.
 */
public class HelloWorldTest {
    static {
        Environment.initializeIfEmpty();
    }

    private final MainClassApplicationUnderTest aut = new MainClassApplicationUnderTest(HelloWorld.class);
    private final Logger log = LoggerFactory.getLogger(getClass());

    @AfterClass
    public void clean_up() {
        aut.close();
    }

    @Test
    public void ping_should_success() {
        log.info("Server started at {}", aut.getAddress().toString());
        assertThat("pong").isEqualTo(testHttpClient(aut).getText("ping"));
    }

    @Test
    public void push_should_success() {
        assertThat("hello achmad").isEqualTo(testHttpClient(aut)
                .requestSpec(request -> request.body(body -> body.text("name=achmad"))).postText("hello"));
    }

    @Test
    public void publish_message_should_success() throws InterruptedException {
        final Promise<String> promise = Promises.prepare(Environment.get());
        final CountDownLatch latch = new CountDownLatch(1);
        promise.onComplete(promised -> latch.countDown());
        final String host = aut.getAddress().getHost();
        final int port = aut.getAddress().getPort();
        final String url = "ws://" + host + ":" + port + "/ws";
        final WebSocketClient client = new WebSocketClient(URI.create(url)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                send("send some data");
                log.info("Connected to {}", url);
            }

            @Override
            public void onMessage(String message) {
                promise.onNext(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {

            }

            @Override
            public void onError(Exception ex) {
                promise.onError(ex);
            }
        };
        client.connect();
        latch.await();
        assertThat(true).isEqualTo(promise.isSuccess());
        assertThat("Message published.").isEqualTo(promise.get());
    }
}