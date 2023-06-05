package com.example.githubapi.exceptions;

public class GithubApiException extends RuntimeException {


    public GithubApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
