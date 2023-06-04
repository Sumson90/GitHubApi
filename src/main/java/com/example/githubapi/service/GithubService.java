package com.example.githubapi.service;

import com.example.githubapi.exceptions.GithubApiException;
import com.example.githubapi.exceptions.RepositoryNotFoundException;
import com.example.githubapi.exceptions.UserNotFoundException;
import com.example.githubapi.models.Branch;
import com.example.githubapi.models.GitHupApiRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


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

    public List<GitHupApiRepository> fetchUserRepositories(String username) throws UserNotFoundException, GithubApiException {
        logger.info("Fetching repositories for user: {}", username);
        Request request = new Request.Builder()
                .url("https://api.github.com/users/" + username + "/repos")
                .addHeader("Authorization", "token " + authToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                if (response.code() == 404) {
                    throw new UserNotFoundException("User not found: " + username);
                } else {
                    throw new GithubApiException("API error: ");
                }
            }
            return Arrays.asList(mapper.readValue(response.body().string(), GitHupApiRepository[].class));
        } catch (IOException e) {
            logger.error("Failed to fetch repositories for user: {}: {}", username, e.getMessage());
            throw new GithubApiException(e.getMessage(), e);
        }
    }

    public List<Branch> fetchRepositoryBranches(String username, String repoName) throws RepositoryNotFoundException, GithubApiException {
        logger.info("Fetching branches for repository: {} of user: {}", repoName, username);
        Request request = new Request.Builder()
                .url("https://api.github.com/repos/" + username + "/" + repoName + "/branches")
                .build();https://api.github.com/repos/

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                if (response.code() == 404) {
                    throw new RepositoryNotFoundException("Repository not found: " + repoName);
                } else {
                    throw new GithubApiException("API error: ");
                }
            }
            return Arrays.asList(mapper.readValue(response.body().string(), Branch[].class));
        } catch (IOException e) {
            logger.error("Failed to fetch branches for repository: {}: {}", repoName, e.getMessage());
            throw new GithubApiException(e.getMessage(), e);
        }
    }
}

