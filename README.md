
## Table of contents
* [General info](#general-info)
* [Requirements](#requirements)
* [Getting Started](#getting-started)
* [Endpoints](#endpoints)
* [Parameters](#parameters)
* [Testing](#testing)
* [Note](#note)


# Github User Repositories API

## General info
This API provides an interface for obtaining information about Github users' repositories. The data includes repository name, owner login, branch names and their latest commit SHA. It returns only non-fork repositories for the requested Github username.

## Requirements
Java 8 or higher
Spring Boot 2.3.x

## Getting Started

Clone this repository.

Navigate to the root directory in your terminal.

Set up a Github access token as an environment variable. Follow these steps to generate your Github token:

In Github, go to your account settings.

Go to the 'Developer settings' section.

Click on 'Personal access tokens'.

Click on 'Generate new token'.

In the 'Note' section, give your token a description.

In the 'Select scopes' section, select the necessary permissions.

Click on 'Generate token' at the bottom of the page.

Copy the token and save it in your environment variables as GITHUB_AUTH_TOKEN.

Run the command mvn spring-boot:run.

To use the application, make a GET request to http://localhost:8080/users/{username}/repos, replacing {username} with the Github username whose repositories you want to get. The
request header should be Accept: application/json.

## Endpoints

GET /users/{username}/repos

Get all repositories of the given Github username.

## Parameters

{username}: The Github username

## Responses:

200 OK: Returns a JSON array of repositories for the valid Github username. Each repository object includes the repository name, owner login, and an array of branches. Each branch object includes its name and the SHA of its latest commit.

404 Not Found: The Github username does not exist or has no repositories. Response includes a status and a message.

406 Not Acceptable: The requested representation is not acceptable. It is currently limited to JSON only. Response includes a status, detail, and the instance URL.

[
    {
        "name": "EquipRental",
        "owner": {
        "login": "Sumson90"
            },
            "branches": [
                {
              "name": "firstCloneBranch",
                "commit": {
                "sha": "c35edc2129b9a989fab24bbcd5a9b1dc20636e47"
                }
            }
        ]
    }
]


## Testing
To run the unit and integration tests, navigate to the root directory in your terminal and run the command mvn test.

## Note
This API uses the Github v3 API as a backing API. You can refer to the official Github v3 API documentation for more details.



