package com.github.achmadns.lab;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import ratpack.form.Form;
import ratpack.websocket.WebSocket;
import ratpack.websocket.WebSocketClose;
import ratpack.websocket.WebSocketHandler;
import ratpack.websocket.WebSocketMessage;

import static ratpack.server.RatpackServer.start;
import static ratpack.spring.Spring.spring;
import static ratpack.websocket.WebSockets.websocket;

@EnableAutoConfiguration
public class HelloWorld implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger log = LoggerFactory.getLogger(HelloWorld.class);

    public static void main(String[] args) throws Exception {
        start(spec -> {
            spec.registry(spring(HelloWorld.class, args));
            spec.handlers(chain -> {
                final ConnectionFactory connectionFactory = chain.getRegistry().get(ConnectionFactory.class);
                final Connection connection = connectionFactory.createConnection();
                chain.prefix("ws", ws -> {
                    ws.all(context -> {
                        websocket(context, new WebSocketHandler<Channel>() {
                            @Override
                            public Channel onOpen(WebSocket webSocket) throws Exception {
                                final Channel channel = connection.createChannel(false);
                                return channel;
                            }

                            @Override
                            public void onClose(WebSocketClose<Channel> webSocketClose) throws Exception {
                                webSocketClose.getOpenResult().close();
                            }

                            @Override
                            public void onMessage(WebSocketMessage<Channel> webSocketMessage) throws Exception {
                                final Channel channel = webSocketMessage.getOpenResult();
                                final String text = webSocketMessage.getText();
                                final WebSocket webSocket = webSocketMessage.getConnection();
                                if (null == text || text.isEmpty()) {
                                    webSocket.send("Message must not empty.");
                                    return;
                                }
                                if (text.startsWith("send")) {
                                    channel.basicPublish("amq.topic", "ws", null, text.getBytes());
                                    webSocket.send("Message published.");
                                    return;
                                }
                            }
                        });
                    });
                });
                chain.prefix("ping", ping -> {
                    ping.all(context -> {
                        context.render("pong");
                    });
                });
                chain.prefix("hello", push -> {
                    push.post(ctx -> {
                        ctx.parse(Form.class)
                                .then(form -> {
                                    final String name = form.get("name");
                                    ctx.render("hello " + name);
                                });
                    });
                });
            });
        });
        log.info("Server configured.");
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Application started.");
        try {
        } catch (Exception e) {
            log.error("Unable to start server", e);
        }
    }
}
