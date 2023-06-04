package com.example.githubapi.controller;

import com.example.githubapi.controllers.GithubController;
import com.example.githubapi.exceptions.ErrorResponse;
import com.example.githubapi.models.Branch;
import com.example.githubapi.models.Commit;
import com.example.githubapi.models.GitHupApiRepository;
import com.example.githubapi.models.User;
import com.example.githubapi.service.GithubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class GithubControllerTest {

    private GithubController githubController;

    @Mock
    private GithubService githubService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        githubController = new GithubController(githubService);
    }

    @Test
    void listUserRepositories_ShouldReturnRepositories() throws Exception {
        // Arrange
        String username = "testuser";
        String acceptHeader = "application/json";

        GitHupApiRepository gitHupApiRepository1 = new GitHupApiRepository();
        gitHupApiRepository1.setName("repo1");
        gitHupApiRepository1.setUser(new User(username, "https://github.com/owner1"));

        GitHupApiRepository gitHupApiRepository2 = new GitHupApiRepository();
        gitHupApiRepository2.setName("repo2");
        gitHupApiRepository2.setUser(new User(username, "https://github.com/owner2"));

        List<GitHupApiRepository> repositories = Arrays.asList(gitHupApiRepository1, gitHupApiRepository2);

        List<Branch> branches1 = Arrays.asList(
                new Branch("branch1", new Commit("commit1"), false),
                new Branch("branch2", new Commit("commit2"), false)
        );

        List<Branch> branches2 = Arrays.asList(
                new Branch("branch3", new Commit("commit3"), false),
                new Branch("branch4", new Commit("commit4"), false)
        );

        when(githubService.fetchUserRepositories(username)).thenReturn(repositories);
        when(githubService.fetchRepositoryBranches(username, gitHupApiRepository1.getName())).thenReturn(branches1);
        when(githubService.fetchRepositoryBranches(username, gitHupApiRepository2.getName())).thenReturn(branches2);

        // Act
        ResponseEntity<?> response = githubController.listUserRepositories(username, acceptHeader);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<GitHupApiRepository> responseRepositories = (List<GitHupApiRepository>) response.getBody();
        assertEquals(2, responseRepositories.size());
        for (GitHupApiRepository gitHupApiRepository : responseRepositories) {
            assertEquals(username, gitHupApiRepository.getUser().getLogin());
            for (Branch branch : gitHupApiRepository.getBranches()) {
                assertEquals(branch.getCommit().getSha(), branch.getCommit().getSha());
            }
        }
    }

    @Test
    void listUserRepositories_InvalidAcceptHeader_ShouldReturn406Response() throws Exception {
        // Arrange
        String username = "testuser";
        String acceptHeader = "application/xml";
        ErrorResponse expectedResponse = new ErrorResponse(406, "Invalid Accept header");

        // Act
        ResponseEntity<?> response = githubController.listUserRepositories(username, acceptHeader);

        // Assert
        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        ErrorResponse actualResponse = (ErrorResponse) response.getBody();
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    }

    @Test
    void listUserRepositories_UserNotFound_ShouldReturn404Response() throws Exception {
        // Arrange
        String username = "nonexistinguser";
        String acceptHeader = "application/json";
        String errorMessage = "User or repository not found";
        ErrorResponse expectedResponse = new ErrorResponse(404, errorMessage);

        when(githubService.fetchUserRepositories(username)).thenThrow(new Exception(errorMessage));

        // Act
        ResponseEntity<?> response = githubController.listUserRepositories(username, acceptHeader);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse actualResponse = (ErrorResponse) response.getBody();
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    }}

