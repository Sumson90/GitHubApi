package com.example.githubapi.exceptions;

public class InvalidAcceptHeaderException extends RuntimeException {
    public InvalidAcceptHeaderException(String message) {
        super(message);
    }
}