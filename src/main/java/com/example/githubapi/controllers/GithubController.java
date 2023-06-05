package com.example.githubapi.controllers;

import com.example.githubapi.exceptions.InvalidAcceptHeaderException;
import com.example.githubapi.models.GitHupApiRepository;
import com.example.githubapi.service.GithubService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController

public class GithubController {

    private final GithubService githubService;

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/users/{username}/repos")
    public Flux<GitHupApiRepository> listUserRepositories(@PathVariable String username, @RequestHeader("Accept") String acceptHeader) {
        if (!"application/json".equalsIgnoreCase(acceptHeader)) {
            throw new InvalidAcceptHeaderException("Invalid Accept header: " + acceptHeader);
        }

        return githubService.fetchUserRepositories(username)
                .flatMap(repository -> {
                    if (!repository.isFork()) {
                        return githubService.fetchRepositoryBranches(username, repository.getName())
                                .collectList()
                                .map(branches -> {
                                    repository.setBranches(branches);
                                    return repository;
                                });
                    } else {
                        return Mono.just(repository);
                    }
                });
    }
}







