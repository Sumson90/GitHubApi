package com.example.githubapi.service;

import com.example.githubapi.exceptions.GithubApiException;
import com.example.githubapi.exceptions.RepositoryNotFoundException;
import com.example.githubapi.exceptions.UserNotFoundException;
import com.example.githubapi.models.Branch;
import com.example.githubapi.models.GitHupApiRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;


@Service
public class GithubService {

    private final WebClient webClient;
    private final ObjectMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(GithubService.class);

    private final String authToken = System.getenv("GITHUB_AUTH_TOKEN");

    public GithubService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "token " + authToken)
                .build();
        this.mapper = new ObjectMapper();
    }

    public Flux<GitHupApiRepository> fetchUserRepositories(String username) {
        logger.info("Fetching repositories for user: {}", username);

        return webClient.get()
                .uri("/users/" + username + "/repos")
                .retrieve()
                .bodyToFlux(GitHupApiRepository.class)
                .onErrorResume(e -> {
                    if (e instanceof WebClientResponseException.NotFound) {
                        return Flux.error(new UserNotFoundException("User not found: " + username));
                    } else {
                        return Flux.error(new GithubApiException("API error: ", e));
                    }
                });
    }

    public Flux<Branch> fetchRepositoryBranches(String username, String repoName) {
        logger.info("Fetching branches for repository: {} of user: {}", repoName, username);

        return webClient.get()
                .uri("/repos/" + username + "/" + repoName + "/branches")
                .retrieve()
                .bodyToFlux(Branch.class)
                .onErrorResume(e -> {
                    if (e instanceof WebClientResponseException.NotFound) {
                        return Flux.error(new RepositoryNotFoundException("Repository not found: " + repoName));
                    } else {
                        return Flux.error(new GithubApiException("API error: ", e));
                    }
                });
    }
}


