# Web Crawler API

## Overview

Web Crawler API is a Java-based HTTP service that searches for a given term across multiple pages of a website. It returns a list of URLs where the term appears.

## How It Works

- The API crawls pages starting from a base URL (set via environment variable).
- It follows links within the same domain.
- For each page, it checks if the search term is present (case-insensitive, matches anywhere in the HTML).
- URLs containing the term are collected and returned.

## API Endpoints

### Start a Search

- **POST** `/crawl`
- **Body:**  
  ```json
  { "keyword": "your_search_term" }
  ```
- **Response:**  
  ```json
  { "id": "search_id" }
  ```
- Starts a new search for the specified term (4-32 characters).

### Get Search Results

- **GET** `/crawl/{id}`
- **Response:**  
  ```json
  {
    "id": "search_id",
    "status": "active|done",
    "urls": [
      "http://example.com/page1.html",
      "http://example.com/page2.html"
    ]
  }
  ```
- Returns URLs found so far for the search. Status is `active` (in progress) or `done` (completed).

## Key Details

- **Base URL:** Set with the `BASE_URL` environment variable.
- **Search Term:** 4-32 characters, case-insensitive.
- **Crawling:** Follows internal links only.
- **Concurrency:** Multiple searches can run at the same time.
- **Persistence:** Results are kept in memory while the app runs.

## Libraries Used

This project uses the following main libraries:

- **Spark Java**: For building the HTTP API.
- **Jsoup**: For parsing and navigating HTML content.
- **Jackson**: For JSON serialization and deserialization.
- **JUnit**: For unit testing.

## Usage

1. **Build Docker image:**
   ```shell
   docker build -t web-crawler-api .
   ```
2. **Run container:**
   ```shell
   docker run -e BASE_URL=http://example.com/ -p 4567:4567 --rm web-crawler-api
   ```

## Getting Started

From the project's root directory, the following commands must be used to compile and run the application.

1.  **Build the Docker image:**
    ```shell
    docker build -t web-crawler-api .
    ```

2.  **Run the Docker container:**
    ```shell
    docker run -e BASE_URL=http://example.com/  -p 4567:4567 --rm web-crawler-api
    ```


