package com.mvinuesa.spring.boot.webflux.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Collection;
import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * This class test de WebSocket {@link UserWebSocketHandler}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserWebSocketHandlerIntegrationTests {

    private static final ObjectMapper json = new ObjectMapper();

    @LocalServerPort
    private String port;

    /**
     * This test connects with WebSocket listen in /user-event, send data and subscribe
     * Result OK
     * 
     * @throws URISyntaxException when URI syntax
     */
    @Test
    public void testUserEventSendSubscribe() throws URISyntaxException {
        int count = 2;
        Flux<String> input = Flux.range(1, count).map(index -> "user-" + index);
        ReplayProcessor<Object> output = ReplayProcessor.create(count);

        WebSocketClient client = new ReactorNettyWebSocketClient();
        client.execute(getUrl("/user-event"),
                session -> session
                        .send(input.map(session::textMessage))
                        .thenMany(session.receive().take(count).map(WebSocketMessage::getPayloadAsText))
                        .subscribeWith(output)
                        .then())
                .block(Duration.ofMillis(3000));

        Collection<Object> events = output.collectList().block(Duration.ofMillis(3000));
        assertEquals(2, events.size());
        Object userEventString = ((List<Object>) events).get(0);
        assertThat(userEventString, hasJsonPath("$.user"));
        assertThat(userEventString, hasJsonPath("$.user.id"));
        assertThat(userEventString, hasJsonPath("$.user.name"));
        assertThat(userEventString, hasJsonPath("$.type"));
        assertThat(userEventString, hasJsonPath("$.when"));
        assertThat(userEventString, hasJsonPath("$.user.name", equalTo("Pepe")));
    }

    protected URI getUrl(String path) throws URISyntaxException {
        return new URI("ws://localhost:" + this.port + path);
    }
}
