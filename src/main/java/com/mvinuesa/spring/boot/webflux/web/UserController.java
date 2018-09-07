package com.mvinuesa.spring.boot.webflux.web;

import com.mvinuesa.spring.boot.webflux.model.User;
import com.mvinuesa.spring.boot.webflux.model.UserEvent;
import com.mvinuesa.spring.boot.webflux.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

/**
 * User {@link RestController}
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    /**
     * Default constructor
     *
     * @param userService {@link UserService}
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Gets a stream of {@link UserEvent}
     *
     * @param id {@link User} id
     * @return {@link Flux} of {@link UserEvent}
     */
    @GetMapping(value = "/{id}/events", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<UserEvent> events(@PathVariable String id) {
        return userService.byId(id)
                .flatMapMany(userService::userEvents);
    }

    /**
     * Gets all {@link User}
     *
     * @return {@link Flux} of {@link User}
     */
    @GetMapping()
    public Flux<User> all() {
        return userService.all();
    }

    /**
     * Gets a {@link User}
     *
     * @param id {@link User} id
     * @return {@link Mono} of {@link User}
     */
    @GetMapping("/{id}")
    public Mono<User> byId(@PathVariable String id) {
        return userService.byId(id);
    }
}

