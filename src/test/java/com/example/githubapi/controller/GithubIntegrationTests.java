package com.example.githubapi.controller;


import com.example.githubapi.models.GitHupApiRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class GithubIntegrationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Value("${github.auth.token}")
    private String githubToken;

    @Test
    public void testFetchUserRepositories() {
        String username = "Sumson90";

        webTestClient.get()
                .uri("/users/{username}/repos", username)
                .header(HttpHeaders.AUTHORIZATION, "token " + githubToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GitHupApiRepository.class)
                .consumeWith(repos -> {
                    assertThat(repos.getResponseBody()).isNotNull();
                    assertThat(repos.getResponseBody().size()).isGreaterThan(0);
                });
    }
    @Test
    public void testUserNotFound() {
        String username = "nonexistentUser";

        webTestClient.get()
                .uri("/users/{username}/repos", username)
                .header(HttpHeaders.AUTHORIZATION, "token " + githubToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("User not found: " + username);
    }



}

