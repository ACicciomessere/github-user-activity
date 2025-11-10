package edu.itba.useractivity.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static edu.itba.useractivity.api.Router.*;

public class Handler { 

    private static void sendErrorResponse(HttpExchange exchange, int statusCode, String backendResponse) throws IOException {
        JSONObject errorJson = new JSONObject();
        
        try {
            // Try to parse the backend error response
            JSONObject backendError = new JSONObject(backendResponse);
            String message = backendError.optString("message", "Unknown error");
            String error = backendError.optString("error", "Error");
            int backendStatus = backendError.optInt("status", statusCode);
            
            errorJson.put("error", error);
            errorJson.put("message", message);
            errorJson.put("status", backendStatus);
            if (backendError.has("timestamp")) {
                errorJson.put("timestamp", backendError.get("timestamp"));
            }
        } catch (Exception e) {
            // If parsing fails, use the raw response
            errorJson.put("error", "Backend Error");
            errorJson.put("message", backendResponse);
            errorJson.put("status", statusCode);
        }

        byte[] response = errorJson.toString().getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    public static class UserEventsHandler implements HttpHandler {
        private final HttpClient httpClient = HttpClient.newHttpClient();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String username = path.substring("/api/user/".length());

            if (username.isEmpty()) {
                sendResponse(exchange, 400, "Username is required");
                return;
            }

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(USER_EVENTS(username)))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    byte[] responseBytes = response.body().getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, responseBytes.length);

                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(responseBytes);
                    }
                } else {
                    sendErrorResponse(exchange, response.statusCode(), response.body());
                }
            } catch (Exception e) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("error", "Connection Error");
                errorJson.put("message", "Error connecting to backend: " + e.getMessage());
                errorJson.put("status", 500);
                
                byte[] response = errorJson.toString().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(500, response.length);
                
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
            }
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
            JSONObject json = new JSONObject();
            json.put("error", message);

            byte[] response = json.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, response.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }

    public static class RepositoryPullRequestsHandler implements HttpHandler {
        private final HttpClient httpClient = HttpClient.newHttpClient();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            // Path format: /api/repository/{owner}/{repository}/pull-requests
            String[] parts = path.substring("/api/repository/".length()).split("/");
            
            if (parts.length < 2) {
                sendResponse(exchange, 400, "Owner and repository are required");
                return;
            }

            String owner = parts[0];
            String repository = parts[1];

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(REPOSITORY_PULL_REQUESTS(owner, repository)))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    byte[] responseBytes = response.body().getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, responseBytes.length);

                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(responseBytes);
                    }
                } else {
                    sendErrorResponse(exchange, response.statusCode(), response.body());
                }
            } catch (Exception e) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("error", "Connection Error");
                errorJson.put("message", "Error connecting to backend: " + e.getMessage());
                errorJson.put("status", 500);
                
                byte[] response = errorJson.toString().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(500, response.length);
                
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
            }
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
            JSONObject json = new JSONObject();
            json.put("error", message);

            byte[] response = json.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, response.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }

    public static class RepositoryMergedPullRequestsHandler implements HttpHandler {
        private final HttpClient httpClient = HttpClient.newHttpClient();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            // Path format: /api/repository/{owner}/{repository}/pull-requests/merged
            String[] parts = path.substring("/api/repository/".length()).split("/");
            
            if (parts.length < 2) {
                sendResponse(exchange, 400, "Owner and repository are required");
                return;
            }

            String owner = parts[0];
            String repository = parts[1];

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(REPOSITORY_MERGED_PULL_REQUESTS(owner, repository)))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    byte[] responseBytes = response.body().getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, responseBytes.length);

                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(responseBytes);
                    }
                } else {
                    sendErrorResponse(exchange, response.statusCode(), response.body());
                }
            } catch (Exception e) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("error", "Connection Error");
                errorJson.put("message", "Error connecting to backend: " + e.getMessage());
                errorJson.put("status", 500);
                
                byte[] response = errorJson.toString().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(500, response.length);
                
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
            }
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
            JSONObject json = new JSONObject();
            json.put("error", message);

            byte[] response = json.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, response.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }

    public static class RepositoryCommitsHandler implements HttpHandler {
        private final HttpClient httpClient = HttpClient.newHttpClient();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            // Path format: /api/repository/{owner}/{repository}/commits
            String[] parts = path.substring("/api/repository/".length()).split("/");
            
            if (parts.length < 2) {
                sendResponse(exchange, 400, "Owner and repository are required");
                return;
            }

            String owner = parts[0];
            String repository = parts[1];

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(REPOSITORY_COMMITS(owner, repository)))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    byte[] responseBytes = response.body().getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, responseBytes.length);

                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(responseBytes);
                    }
                } else {
                    sendErrorResponse(exchange, response.statusCode(), response.body());
                }
            } catch (Exception e) {
                JSONObject errorJson = new JSONObject();
                errorJson.put("error", "Connection Error");
                errorJson.put("message", "Error connecting to backend: " + e.getMessage());
                errorJson.put("status", 500);
                
                byte[] response = errorJson.toString().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(500, response.length);
                
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
            }
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
            JSONObject json = new JSONObject();
            json.put("error", message);

            byte[] response = json.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, response.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }
}
