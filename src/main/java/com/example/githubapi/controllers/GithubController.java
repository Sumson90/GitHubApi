package com.example.githubapi.controllers;

import com.example.githubapi.exceptions.ErrorResponse;
import com.example.githubapi.exceptions.GithubApiException;
import com.example.githubapi.exceptions.RepositoryNotFoundException;
import com.example.githubapi.exceptions.UserNotFoundException;
import com.example.githubapi.models.GitHupApiRepository;
import com.example.githubapi.service.GithubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(GithubController.class);

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/users/{username}/repos")
    public ResponseEntity<?> listUserRepositories(@PathVariable String username, @RequestHeader("Accept") String acceptHeader)
            throws UserNotFoundException, RepositoryNotFoundException, GithubApiException {
        logger.info("Received request to list repositories for user: {}", username);
        if (!acceptHeader.equals("application/json")) {
            logger.warn("Invalid Accept header: {}", acceptHeader);
            return ResponseEntity.status(406).body(new ErrorResponse(406, "Invalid Accept header"));
        }

        List<GitHupApiRepository> repositories = githubService.fetchUserRepositories(username);
        for (GitHupApiRepository gitHupApiRepository : repositories) {
            if (!gitHupApiRepository.isFork()) {
                gitHupApiRepository.setBranches(githubService.fetchRepositoryBranches(username, gitHupApiRepository.getName()));
            }
        }
        repositories = repositories.stream().filter(repo -> !repo.isFork()).collect(Collectors.toList());
        logger.info("Successfully fetched {} repositories for user: {}", repositories.size(), username);
        return ResponseEntity.ok(repositories);
    }
}





