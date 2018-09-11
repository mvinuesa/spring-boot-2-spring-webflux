package com.mvinuesa.spring.boot.webflux;

import com.mvinuesa.spring.boot.webflux.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

/**
 * Spring application integration tests
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringApplicationIntegrationTests {

    @Autowired
    private WebTestClient webTestClient;

    /**
     * This test gets all user with Functional Model and Annotation Model
     * Result OK
     */
    @Test
    public void getAllUsers() {
        webTestClient.get().uri("/users").accept(APPLICATION_JSON).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBodyList(User.class)
                .consumeWith(result -> assertEquals(3, Objects.requireNonNull(result.getResponseBody()).size()));

        webTestClient.get().uri("/routeUsers").accept(APPLICATION_JSON).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBodyList(User.class)
                .consumeWith(result -> assertEquals(3, Objects.requireNonNull(result.getResponseBody()).size()));
    }

    /**
     * This test gets user by id with Functional Model and Annotation Model
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

        User routeUser = Objects.requireNonNull(webTestClient.get().uri("/routeUsers").accept(APPLICATION_JSON).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBodyList(User.class)
                .consumeWith(result -> assertEquals(3, Objects.requireNonNull(result.getResponseBody()).size()))
                .returnResult().getResponseBody()).get(0);


        webTestClient.get().uri("/routeUsers/" + routeUser.getId()).accept(APPLICATION_JSON).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody(User.class)
                .consumeWith(result -> {
                    assertEquals(routeUser.getId(), Objects.requireNonNull(result.getResponseBody()).getId());
                    assertEquals(routeUser.getName(), result.getResponseBody().getName());
                });
    }
}
