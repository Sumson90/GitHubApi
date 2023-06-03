package com.example.githubapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repository {
    private String name;
    private User owner;
    private List<Branch> branches;
    private boolean fork;

    public boolean isFork() {
        return this.fork;
    }



}

