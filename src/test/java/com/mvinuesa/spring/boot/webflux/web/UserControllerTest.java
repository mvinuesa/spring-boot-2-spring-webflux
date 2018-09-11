package com.mvinuesa.spring.boot.webflux.web;

import com.mvinuesa.spring.boot.webflux.model.User;
import com.mvinuesa.spring.boot.webflux.model.UserEvent;
import com.mvinuesa.spring.boot.webflux.service.UserService;
import com.mvinuesa.spring.boot.webflux.web.UserController;

import org.junit.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

/**
 * This class test {@link UserController}
 */
public class UserControllerTest {

    private final WebTestClient webTestClient = WebTestClient
            .bindToController(new UserController(new UserService())).build();

    /**
     * This test gets all user
     * Result OK
     */
    @Test
    public void getAllUsers() {
        webTestClient.get().uri("/users").accept(APPLICATION_JSON).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBodyList(User.class)
                .consumeWith(result -> assertEquals(3, Objects.requireNonNull(result.getResponseBody()).size()));
    }

    /**
     * This test gets all user and assert json body
     * Result OK
     */
    @Test
    public void getAllUsersJson() {
        webTestClient.get().uri("/users").accept(APPLICATION_JSON).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Pepe")
                .jsonPath("$[1].name").isEqualTo("Juan")
                .jsonPath("$[2].name").isEqualTo("Ricardo");
    }


    /**
     * This test get user by id
     * Result OK
     */
    @Test
    public void getUserById() {
        User user = Objects.requireNonNull(webTestClient.get().uri("/users").accept(APPLICATION_JSON).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBodyList(User.class)
                .consumeWith(result -> assertEquals(3, Objects.requireNonNull(result.getResponseBody()).size()))
                .returnResult().getResponseBody()).get(0);


        webTestClient.get().uri("/users/" + user.getId()).accept(APPLICATION_JSON).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(User.class)
                .consumeWith(result -> {
                    assertEquals(user.getId(), Objects.requireNonNull(result.getResponseBody()).getId());
                    assertEquals(user.getName(), result.getResponseBody().getName());
                });
    }

    /**
     * Tests {@link UserEvent} in a event stream
     * Result OK
     */
    @Test
    public void getEventsUserById() {
        User user = Objects.requireNonNull(webTestClient.get().uri("/users").accept(APPLICATION_JSON).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBodyList(User.class)
                .consumeWith(result -> assertEquals(3, Objects.requireNonNull(result.getResponseBody()).size()))
                .returnResult().getResponseBody()).get(0);


        Flux<UserEvent> userEventFlux =  webTestClient.get()
                .uri("/users/" + user.getId() + "/events")
                .accept(TEXT_EVENT_STREAM).exchange()
                .expectStatus().isOk()
                .returnResult(UserEvent.class)
                .getResponseBody();

        StepVerifier.create(userEventFlux)
                .consumeNextWith(userEvent -> {
                    assertEquals(user, userEvent.getUser());
                    assertNotNull(userEvent.getWhen());
                    assertNotNull(userEvent.getType());
                })
                .thenCancel()
                .verify();
    }
}
