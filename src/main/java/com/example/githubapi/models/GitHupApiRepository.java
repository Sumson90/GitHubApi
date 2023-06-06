package com.example.githubapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Repository
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHupApiRepository {
    private String name;
    private List<Branch> branches;
    @JsonIgnore
    private boolean fork;
    private Owner owner;

    private static final Logger logger = LoggerFactory.getLogger(GitHupApiRepository.class);

    public boolean isFork() {
        return fork;
    }


}


