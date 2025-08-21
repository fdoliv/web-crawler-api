# Web Crawler API

## Objective

This project is a Java application designed to navigate a website, search for a user-provided term, and list the URLs where the term is found.

## Features

### 1. API Interaction

User interaction is handled through an HTTP API available on port 4567. It supports the following operations:

#### a. Start a New Search (POST)

* **Endpoint:** `/crawl`
* **Method:** `POST`
* **Description:** Initiates a new search for a given keyword.
* **Request Body:**
    ```json
    {
      "keyword": "security"
    }
    ```
* **Success Response:**
    * **Code:** 200 OK
    * **Body:**
        ```json
        {
          "id": "30vbllyb"
        }
        ```

#### b. Query Search Results (GET)

* **Endpoint:** `/crawl/{id}`
* **Method:** `GET`
* **Description:** Queries the results of a previously initiated search.
* **Success Response:**
    * **Code:** 200 OK
    * **Body:**
        ```json
        {
          "id": "30vbllyb",
          "status": "active",
          "urls": [
            "[http://example.com/index2.html](http://example.com/index2.html)",
            "[http://example.com/page1.html](http://example.com/page1.html)"
          ]
        }
        ```

### 2. Search Term Constraints

* The search term must be between 4 and 32 characters long.
* The search is case-insensitive and performed on the entire HTML content, including tags and comments.

### 3. Search ID

* The search ID is an automatically generated 8-character alphanumeric code.

### 4. Base URL and Crawling

* The base URL for the website to be crawled is determined by an environment variable.
* The application follows both absolute and relative links found in anchor elements, but only if they share the same base URL.

### 5. Concurrency and Data Persistence

* The application supports running multiple searches simultaneously.
* Information about ongoing (`active`) and completed (`done`) searches is retained for the duration of the application's execution.

### 6. Partial Results

* While a search is in progress, the GET operation will return any partial results that have been found so far.

### 7. Project Structure

* The provided base project structure must be used.
* The `Dockerfile` and `pom.xml` files cannot be modified. Any other provided files can be changed as needed.

## Getting Started

From the project's root directory, the following commands must be used to compile and run the application.

1.  **Build the Docker image:**
    ```shell
    docker build -t web-crawler-api .
    ```

2.  **Run the Docker container:**
    ```shell
    docker run -e BASE_URL=[http://example.com/](http://example.com/) -p 4567:4567 --rm web-crawler-api
    ```

## Code Distribution

* The source code should be delivered in a `.tar` or `.tar.gz` archive.
* If the code is made public, it is kindly requested that any project-specific references (including in package names and hosts) be removed from all files before publication.
