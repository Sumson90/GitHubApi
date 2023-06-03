package com.example.githubapi.service;

import com.example.githubapi.models.Branch;
import com.example.githubapi.models.Repository;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GithubService {
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Repository> fetchUserRepositories(String username) throws Exception {
        Request request = new Request.Builder()
                .url("https://api.github.com/users/" + username + "/repos")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                handleUnsuccessfulResponse(response);
            }
            return Arrays.asList(mapper.readValue(response.body().string(), Repository[].class));
        }
    }

    public List<Branch> fetchRepositoryBranches(String username, String repoName) throws Exception {
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
        if (response.code() == 404) {
            throw new Exception("User or repository not found");
        } else if (response.code() == 403) {
            throw new Exception("Forbidden");
        } else {
            throw new Exception("Unknown error");
        }
    }
}
