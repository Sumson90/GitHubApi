package com.example.githubapi.service;

import com.example.githubapi.models.Branch;
import com.example.githubapi.models.GitHupApiRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
@AllArgsConstructor

@Service
public class GithubService {
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(GithubService.class);

    private final String authToken = System.getenv("GITHUB_AUTH_TOKEN");

    public GithubService() {
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();
    }

    public List<GitHupApiRepository> fetchUserRepositories(String username) throws Exception {
        logger.info("Fetching repositories for user: {}", username);
        Request request = new Request.Builder()
                .url("https://api.github.com/users/" + username + "/repos")
                .addHeader("Authorization", "token " + authToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                handleUnsuccessfulResponse(response);
            }
            return Arrays.asList(mapper.readValue(response.body().string(), GitHupApiRepository[].class));
        }
    }

    public List<Branch> fetchRepositoryBranches(String username, String repoName) throws Exception {
        logger.info("Fetching branches for repository: {} of user: {}", repoName, username);
        Request request = new Request.Builder()
                .url("https://api.github.com/repos/" + username + "/" + repoName + "/branches")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                handleUnsuccessfulResponse(response);
            }
            return Arrays.asList(mapper.readValue(response.body().string(), Branch[].class));
        }
    }

    private void handleUnsuccessfulResponse(Response response) throws Exception {
        String message = response.body() != null ? response.body().string() : "Unknown error";
        logger.error("Unsuccessful response from GitHub API: {}", message);
        switch (response.code()) {
            case 404:
                throw new Exception("User or repository not found: " + message);
            case 403:
                throw new Exception("Forbidden: " + message);
            default:
                throw new Exception("API error: " + message);
        }
    }
}



