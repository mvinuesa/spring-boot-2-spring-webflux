package com.mvinuesa.spring.boot.webflux.web;

import com.mvinuesa.spring.boot.webflux.model.User;
import com.mvinuesa.spring.boot.webflux.model.UserEvent;
import com.mvinuesa.spring.boot.webflux.service.UserService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * User Router Configuration
 */
@Configuration
public class UserRouterConfiguration {

    /**
     * Defines the Functional Programming Model equals to {@link UserController} Annotation Model
     *
     * @param userService {@link UserService}
     *
     * @return {@link RouterFunction} of {@link ServerResponse}
     */
    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserService userService) {
        return route(GET("/routeUsers"),
                request -> ok().body(userService.all(), User.class))
                .andRoute(GET("/routeUsers/{id}"),
                        request -> ok().body(userService.byId(request.pathVariable("id")), User.class))
                .andRoute(GET("/routeUsers/{id}/events"),
                        request -> ok().contentType(TEXT_EVENT_STREAM)
                                .body(userService.byId(request.pathVariable("id"))
                                        .flatMapMany(userService::userEvents), UserEvent.class));
    }
}