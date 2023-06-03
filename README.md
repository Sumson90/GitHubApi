Github User Repositories API
Overview
This API provides an interface for obtaining information about Github users' repositories. The data includes repository name, owner login, branch names and their latest commit SHA. It returns only non-fork repositories for the requested Github username.

Requirements
Java 8 or higher
Spring Boot 2.3.x
Getting Started
To run the application:

Clone this repository.
Navigate to the root directory in your terminal.
Run the command mvn spring-boot:run.

To use the application:

Make a GET request to http://localhost:8080/users/{username}/repos, replacing {username} with the Github username whose repositories you want to get. The request header should be Accept: application/json.
Endpoints
GET /users/{username}/repos
Get all repositories of the given Github username.

Parameters:

{username}: The Github username
Responses:

200 OK: Returns a JSON array of repositories for the valid Github username. Each repository object includes the repository name, owner login, and an array of branches. Each branch object includes its name and the SHA of its latest commit.
Example response:

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

404 Not Found: The Github username does not exist or has no repositories. Response includes a status and a message.
Example response:

{
"status": 404,
"message": "User or repository not found"
}

406 Not Acceptable: The requested representation is not acceptable. It is currently limited to JSON only. Response includes a status, detail, and the instance URL.
Example response:


{
"type": "about:blank",
"title": "Not Acceptable",
"status": 406,
"detail": "Acceptable representations: [application/json, application/*+json].",
"instance": "/users/Sumson90/repos"
}
Note
This API uses the Github v3 API as a backing API. You can refer to the official Github v3 API documentation for more details.