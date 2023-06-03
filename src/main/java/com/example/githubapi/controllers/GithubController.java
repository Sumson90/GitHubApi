package com.example.githubapi.controllers;


import com.example.githubapi.exceptions.ErrorResponse;
import com.example.githubapi.models.Repository;
import com.example.githubapi.service.GithubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
public class GithubController {

    private final GithubService githubService;

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/users/{username}/repos")
    public ResponseEntity<?> listUserRepositories(@PathVariable String username, @RequestHeader("Accept") String acceptHeader) {
        if (!acceptHeader.equals("application/json")) {
            return ResponseEntity.status(406).body(new ErrorResponse(406, "Invalid Accept header"));
        }

        try {
            List<Repository> repositories = githubService.fetchUserRepositories(username);
            for (Repository repository : repositories) {
                if (!repository.isFork()) {
                    repository.setBranches(githubService.fetchRepositoryBranches(username, repository.getName()));
                }
            }
            repositories = repositories.stream().filter(repository -> !repository.isFork()).collect(Collectors.toList());
            return ResponseEntity.ok(repositories);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, e.getMessage()));
        }
    }
}


