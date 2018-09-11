package com.mvinuesa.spring.boot.webflux.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket configuration class
 */
@Configuration
public class UserWebSocketConfiguration {

    /**
     * User WebSocket {@link HandlerMapping}
     * @param userWebSocketHandler {@link UserWebSocketHandler}
     *
     * @return {@link HandlerMapping}
     */
    @Bean
    public HandlerMapping webSocketHandlerMapping(UserWebSocketHandler userWebSocketHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/user-event", userWebSocketHandler);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

    /**
     * WebSocket Handler Adapter
     * @return {@link WebSocketHandlerAdapter}
     */
    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

}