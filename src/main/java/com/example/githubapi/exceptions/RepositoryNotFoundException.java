package com.example.githubapi.exceptions;

public class RepositoryNotFoundException extends RuntimeException{
    public RepositoryNotFoundException(String message) {
        super(message);
    }
}
