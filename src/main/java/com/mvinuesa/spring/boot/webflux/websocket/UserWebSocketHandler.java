package com.mvinuesa.spring.boot.webflux.websocket;

import com.mvinuesa.spring.boot.webflux.service.UserService;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Mono;

/**
 *  User {@link WebSocketHandler}
 */
@Component
public class UserWebSocketHandler implements WebSocketHandler {

    private final UserService userService;

    /**
     * Default constructor
     * @param userService {@link UserService}
     */
    public UserWebSocketHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession
                .send(userService.textUserEventsFirst()
                .map(webSocketSession::textMessage))
                .and(webSocketSession
                        .receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .log());
    }
}