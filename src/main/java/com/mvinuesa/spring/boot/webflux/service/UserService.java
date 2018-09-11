package com.mvinuesa.spring.boot.webflux.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvinuesa.spring.boot.webflux.model.User;
import com.mvinuesa.spring.boot.webflux.model.UserEvent;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * User Service simulate repository access
 */
@Service
public class UserService {

    private final Collection<User> users;
    private static final ObjectMapper json = new ObjectMapper();

    /**
     * Default constructor
     */
    public UserService() {
        users = Stream.of("Pepe", "Juan", "Ricardo")
                .map(name -> new User(UUID.randomUUID().toString(), name))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * sends a {@link UserEvent} every one second
     */
    public Flux<UserEvent> userEvents(User user) {
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

        Flux<UserEvent> userEventFlux = Flux.fromStream(Stream.generate(() ->
                new UserEvent(user, new Date(), randomType())));

        return Flux.zip(interval, userEventFlux)
                .map(Tuple2::getT2);
    }

    /**
     * sends a {@link UserEvent} every one second
     */
    public Flux<String> textUserEventsFirst() {
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

        Flux<String> userEventFlux = Flux.fromStream(Stream.generate(() ->
        {
            try {
                return json.writeValueAsString(new UserEvent(users.stream().findFirst()
                        .orElse(new User("dummyId", "dummy")), new Date(), randomType()));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return "{\"dummy\"}";
            }
        }));

        return Flux.zip(interval, userEventFlux)
                .map(Tuple2::getT2);
    }

    /**
     * gets all users
     *
     * @return {@link Flux} of {@link User}
     */
    public Flux<User> all() {
        return Flux.fromIterable(users);
    }

    /**
     * gets a {@link User} by id
     *
     * @return {@link Mono} of {@link User}
     */
    public Mono<User> byId(String id) {
        return Mono.just(Objects.requireNonNull(users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null)));
    }

    private String randomType() {
        String[] types = "new account,delete account,update account".split(",");
        return types[new Random().nextInt(types.length)];
    }
}
