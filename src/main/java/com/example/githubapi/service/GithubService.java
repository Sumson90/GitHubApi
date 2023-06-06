package com.example.githubapi.service;

import com.example.githubapi.exceptions.GithubApiException;
import com.example.githubapi.exceptions.RepositoryNotFoundException;
import com.example.githubapi.exceptions.UserNotFoundException;
import com.example.githubapi.models.Branch;
import com.example.githubapi.models.GitHupApiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

@Service
public class GithubService {


    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(GithubService.class);

    private final String authToken;

    public GithubService(WebClient.Builder webClientBuilder, @Value("${github.auth.token}") String authToken) {
        this.webClient = webClientBuilder.baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "token " + authToken)
                .build();
        this.authToken = authToken;
    }

    public Flux<GitHupApiRepository> fetchUserRepositories(String username) {
        logger.info("Fetching repositories for user: {}", username);

        return webClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .bodyToFlux(GitHupApiRepository.class)
                .onErrorResume(WebClientResponseException.NotFound.class, e -> Flux.error(new UserNotFoundException("User not found: " + username)))
                .onErrorResume(WebClientResponseException.class, e -> Flux.error(new GithubApiException("API error: ", e)));
    }


    public Flux<Branch> fetchRepositoryBranches(String username, String repoName) {
        logger.info("Fetching branches for repository: {} of user: {}", repoName, username);

        return webClient.get()
                .uri("/repos/{username}/{repoName}/branches", username, repoName)
                .retrieve()
                .bodyToFlux(Branch.class)
                .onErrorResume(WebClientResponseException.NotFound.class, e -> Flux.error(new RepositoryNotFoundException("Repository not found: " + repoName)))
                .onErrorResume(WebClientResponseException.class, e -> Flux.error(new GithubApiException("API error: ", e)));
    }


}


