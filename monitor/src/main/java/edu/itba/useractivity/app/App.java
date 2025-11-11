package edu.itba.useractivity.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import edu.itba.useractivity.api.Handler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class App {
    private static final int MONITOR_PORT = 3030;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(MONITOR_PORT), 0);

        server.createContext("/", new RootHandler());
        server.createContext("/api/user/", new Handler.UserEventsHandler());
        server.createContext("/api/repository/", new RepositoryHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("Monitor Server started on http://localhost:" + MONITOR_PORT);
        System.out.println("Backend expected at http://localhost:8080");
        System.out.println("Access the monitor at http://localhost:" + MONITOR_PORT);
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>GitHub User Activity Monitor</title>
                    <style>
                        * {
                            margin: 0;
                            padding: 0;
                            box-sizing: border-box;
                        }
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            min-height: 100vh;
                            padding: 20px;
                        }
                        .container {
                            max-width: 1200px;
                            margin: 0 auto;
                        }
                        h1 {
                            color: white;
                            text-align: center;
                            margin-bottom: 30px;
                            font-size: 2.5em;
                            text-shadow: 2px 2px 4px rgba(0,0,0,0.2);
                        }
                        .search-container {
                            background: white;
                            padding: 30px;
                            border-radius: 10px;
                            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
                            margin-bottom: 30px;
                        }
                        .search-box {
                            display: flex;
                            gap: 10px;
                            margin-bottom: 10px;
                        }
                        input[type="text"] {
                            flex: 1;
                            padding: 12px 20px;
                            font-size: 16px;
                            border: 2px solid #e0e0e0;
                            border-radius: 5px;
                            outline: none;
                            transition: border-color 0.3s;
                        }
                        input[type="text"]:focus {
                            border-color: #667eea;
                        }
                        button {
                            padding: 12px 30px;
                            font-size: 16px;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            color: white;
                            border: none;
                            border-radius: 5px;
                            cursor: pointer;
                            transition: transform 0.2s;
                        }
                        button:hover {
                            transform: translateY(-2px);
                        }
                        button:active {
                            transform: translateY(0);
                        }
                        #loading {
                            display: none;
                            text-align: center;
                            color: #667eea;
                            font-size: 18px;
                            margin: 20px 0;
                        }
                        #error {
                            display: none;
                            background: #fee;
                            color: #c33;
                            padding: 15px;
                            border-radius: 5px;
                            margin: 20px 0;
                            border-left: 4px solid #c33;
                        }
                        #results {
                            display: grid;
                            gap: 20px;
                        }
                        .event-card {
                            background: white;
                            padding: 20px;
                            border-radius: 10px;
                            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
                            transition: transform 0.2s;
                        }
                        .event-card:hover {
                            transform: translateY(-5px);
                            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
                        }
                        .event-header {
                            display: flex;
                            align-items: center;
                            justify-content: space-between;
                            margin-bottom: 15px;
                        }
                        .event-type {
                            display: inline-block;
                            padding: 6px 12px;
                            border-radius: 20px;
                            font-size: 14px;
                            font-weight: bold;
                            color: white;
                        }
                        .event-type.PUSH_EVENT { background: #28a745; }
                        .event-type.CREATE_EVENT { background: #007bff; }
                        .event-type.PULL_REQUEST_EVENT { background: #6f42c1; }
                        .event-type.FORK_EVENT { background: #fd7e14; }
                        .event-type.WATCH_EVENT { background: #ffc107; }
                        .event-type.ISSUE_EVENT { background: #dc3545; }
                        .event-date {
                            color: #666;
                            font-size: 14px;
                        }
                        .event-body {
                            color: #333;
                        }
                        .event-actor {
                            font-weight: bold;
                            color: #667eea;
                        }
                        .event-repo {
                            color: #764ba2;
                            font-weight: 500;
                        }
                        .event-details {
                            margin-top: 10px;
                            padding-top: 10px;
                            border-top: 1px solid #e0e0e0;
                            font-size: 14px;
                            color: #666;
                        }
                        #repo-loading {
                            display: none;
                            text-align: center;
                            color: #667eea;
                            font-size: 18px;
                            margin: 20px 0;
                        }
                        #repo-error {
                            display: none;
                            background: #fee;
                            color: #c33;
                            padding: 15px;
                            border-radius: 5px;
                            margin: 20px 0;
                            border-left: 4px solid #c33;
                        }
                        #repo-results {
                            display: grid;
                            gap: 20px;
                        }
                        .pr-card, .commit-card {
                            background: white;
                            padding: 20px;
                            border-radius: 10px;
                            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
                            transition: transform 0.2s;
                        }
                        .pr-card:hover, .commit-card:hover {
                            transform: translateY(-5px);
                            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
                        }
                        .pr-header, .commit-header {
                            display: flex;
                            align-items: center;
                            justify-content: space-between;
                            margin-bottom: 15px;
                        }
                        .pr-number {
                            display: inline-block;
                            padding: 6px 12px;
                            border-radius: 20px;
                            font-size: 14px;
                            font-weight: bold;
                            background: #6f42c1;
                            color: white;
                        }
                        .pr-state {
                            display: inline-block;
                            padding: 6px 12px;
                            border-radius: 20px;
                            font-size: 14px;
                            font-weight: bold;
                            color: white;
                        }
                        .pr-state.open { background: #28a745; }
                        .pr-state.closed { background: #dc3545; }
                        .pr-state.merged { background: #6f42c1; }
                        .commit-sha {
                            font-family: monospace;
                            color: #667eea;
                            font-weight: bold;
                        }
                        .commit-message {
                            color: #333;
                            margin: 10px 0;
                        }
                        .commit-author {
                            color: #666;
                            font-size: 14px;
                        }
                        .button-group button {
                            flex: 1;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>📊 GitHub User Activity Monitor</h1>
                        <h2 style="color: white; text-align: center; margin-bottom: 20px; font-size: 1.5em; text-shadow: 1px 1px 2px rgba(0,0,0,0.2);">User Events</h2>
                        
                        <div class="search-container">
                            <div class="search-box">
                                <input type="text" id="username" placeholder="Enter GitHub username..." value="octocat">
                                <button onclick="fetchEvents()">🔍 Search</button>
                                <button onclick="clearEvents()" style="background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);">🗑️ Clear</button>
                            </div>
                            <div id="loading">Loading...</div>
                            <div id="error"></div>
                        </div>
                        
                        <div id="results"></div>
                    </div>
                    
                    <div class="container">
                        <h1 style="margin-top: 50px;">📦 Repository Information</h1>
                        
                        <div class="search-container">
                            <div class="search-box">
                                <input type="text" id="owner" placeholder="Owner (e.g., octocat)" value="octocat">
                                <input type="text" id="repository" placeholder="Repository (e.g., Hello-World)" value="Hello-World">
                            </div>
                            <div class="button-group" style="display: flex; gap: 10px; margin-top: 10px;">
                                <button onclick="fetchPullRequests()">🔀 Pull Requests</button>
                                <button onclick="fetchMergedPullRequests()">✅ Merged PRs</button>
                                <button onclick="fetchCommits()">📝 Commits</button>
                            </div>
                            <div id="repo-loading">Loading...</div>
                            <div id="repo-error"></div>
                        </div>
                        
                        <div id="repo-results"></div>
                    </div>

                    <script>
                        const usernameInput = document.getElementById('username');
                        
                        usernameInput.addEventListener('keypress', function(e) {
                            if (e.key === 'Enter') {
                                fetchEvents();
                            }
                        });

                        async function fetchEvents() {
                            const username = usernameInput.value.trim();
                            
                            if (!username) {
                                showError('Please enter a username');
                                return;
                            }

                            const loading = document.getElementById('loading');
                            const error = document.getElementById('error');
                            const results = document.getElementById('results');

                            loading.style.display = 'block';
                            error.style.display = 'none';
                            results.innerHTML = '';

                            try {
                                const response = await fetch(`/api/user/${username}`);
                                
                                if (!response.ok) {
                                    const errorData = await response.json();
                                    loading.style.display = 'none';
                                    showErrorFromResponse(errorData);
                                    return;
                                }
                                
                                const events = await response.json();
                                loading.style.display = 'none';
                                
                                if (events.length === 0) {
                                    showError('No events found for this user');
                                    return;
                                }
                                
                                displayEvents(events);
                            } catch (err) {
                                loading.style.display = 'none';
                                showError('Failed to fetch events: ' + err.message + '. Make sure the backend is running on localhost:8080');
                            }
                        }

                        function showError(message) {
                            const error = document.getElementById('error');
                            error.innerHTML = `<strong>Error:</strong> ${message}`;
                            error.style.display = 'block';
                        }

                        function showErrorFromResponse(errorData) {
                            const error = document.getElementById('error');
                            const errorType = errorData.error || 'Error';
                            const message = errorData.message || 'Unknown error occurred';
                            const status = errorData.status || 'Unknown';
                            
                            let errorHtml = `<strong>${errorType}</strong> (Status: ${status})<br>`;
                            errorHtml += `<span style="margin-top: 5px; display: block;">${message}</span>`;
                            
                            error.innerHTML = errorHtml;
                            error.style.display = 'block';
                        }

                        function clearEvents() {
                            const results = document.getElementById('results');
                            const error = document.getElementById('error');
                            const loading = document.getElementById('loading');
                            
                            results.innerHTML = '';
                            error.style.display = 'none';
                            loading.style.display = 'none';
                        }

                        function displayEvents(events) {
                            const results = document.getElementById('results');
                            
                            events.forEach(event => {
                                const card = document.createElement('div');
                                card.className = 'event-card';
                                
                                const eventType = event.type.replace(/_/g, ' ');
                                const date = new Date(event.createdAt).toLocaleString();
                                
                                let details = '';
                                if (event.type === 'PUSH_EVENT' && event.commits) {
                                    details = `<div class="event-details">📝 ${event.commits.length} commit(s)</div>`;
                                } else if (event.type === 'PULL_REQUEST_EVENT' && event.pullRequest) {
                                    details = `<div class="event-details">🔀 PR #${event.pullRequest.number}: ${event.pullRequest.title}</div>`;
                                } else if (event.type === 'CREATE_EVENT' && event.refType) {
                                    details = `<div class="event-details">✨ Created ${event.refType}</div>`;
                                }
                                
                                card.innerHTML = `
                                    <div class="event-header">
                                        <span class="event-type ${event.type}">${eventType}</span>
                                        <span class="event-date">${date}</span>
                                    </div>
                                    <div class="event-body">
                                        <span class="event-actor">${event.actor.username}</span>
                                        performed 
                                        <span class="event-actor">${event.type}</span>
                                        on
                                        <span class="event-repo">${event.repo.name}</span>
                                    </div>
                                    ${details}
                                `;
                                
                                results.appendChild(card);
                            });
                        }

                        // Repository functions
                        async function fetchPullRequests() {
                            const owner = document.getElementById('owner').value.trim();
                            const repository = document.getElementById('repository').value.trim();
                            
                            if (!owner || !repository) {
                                showRepoError('Please enter both owner and repository');
                                return;
                            }

                            const loading = document.getElementById('repo-loading');
                            const error = document.getElementById('repo-error');
                            const results = document.getElementById('repo-results');

                            loading.style.display = 'block';
                            error.style.display = 'none';
                            results.innerHTML = '';

                            try {
                                const response = await fetch(`/api/repository/${owner}/${repository}/pull-requests`);
                                
                                if (!response.ok) {
                                    const errorData = await response.json();
                                    loading.style.display = 'none';
                                    showRepoErrorFromResponse(errorData);
                                    return;
                                }
                                
                                const pullRequests = await response.json();
                                loading.style.display = 'none';
                                
                                if (pullRequests.length === 0) {
                                    showRepoError('No pull requests found for this repository');
                                    return;
                                }
                                
                                displayPullRequests(pullRequests);
                            } catch (err) {
                                loading.style.display = 'none';
                                showRepoError('Failed to fetch pull requests: ' + err.message + '. Make sure the backend is running on localhost:8080');
                            }
                        }

                        async function fetchMergedPullRequests() {
                            const owner = document.getElementById('owner').value.trim();
                            const repository = document.getElementById('repository').value.trim();
                            
                            if (!owner || !repository) {
                                showRepoError('Please enter both owner and repository');
                                return;
                            }

                            const loading = document.getElementById('repo-loading');
                            const error = document.getElementById('repo-error');
                            const results = document.getElementById('repo-results');

                            loading.style.display = 'block';
                            error.style.display = 'none';
                            results.innerHTML = '';

                            try {
                                const response = await fetch(`/api/repository/${owner}/${repository}/pull-requests/merged`);
                                
                                if (!response.ok) {
                                    const errorData = await response.json();
                                    loading.style.display = 'none';
                                    showRepoErrorFromResponse(errorData);
                                    return;
                                }
                                
                                const pullRequests = await response.json();
                                loading.style.display = 'none';
                                
                                if (pullRequests.length === 0) {
                                    showRepoError('No merged pull requests found for this repository');
                                    return;
                                }
                                
                                displayPullRequests(pullRequests);
                            } catch (err) {
                                loading.style.display = 'none';
                                showRepoError('Failed to fetch merged pull requests: ' + err.message + '. Make sure the backend is running on localhost:8080');
                            }
                        }

                        async function fetchCommits() {
                            const owner = document.getElementById('owner').value.trim();
                            const repository = document.getElementById('repository').value.trim();
                            
                            if (!owner || !repository) {
                                showRepoError('Please enter both owner and repository');
                                return;
                            }

                            const loading = document.getElementById('repo-loading');
                            const error = document.getElementById('repo-error');
                            const results = document.getElementById('repo-results');

                            loading.style.display = 'block';
                            error.style.display = 'none';
                            results.innerHTML = '';

                            try {
                                const response = await fetch(`/api/repository/${owner}/${repository}/commits`);
                                
                                if (!response.ok) {
                                    const errorData = await response.json();
                                    loading.style.display = 'none';
                                    showRepoErrorFromResponse(errorData);
                                    return;
                                }
                                
                                const commits = await response.json();
                                loading.style.display = 'none';
                                
                                if (commits.length === 0) {
                                    showRepoError('No commits found for this repository');
                                    return;
                                }
                                
                                displayCommits(commits);
                            } catch (err) {
                                loading.style.display = 'none';
                                showRepoError('Failed to fetch commits: ' + err.message + '. Make sure the backend is running on localhost:8080');
                            }
                        }

                        function showRepoError(message) {
                            const error = document.getElementById('repo-error');
                            error.innerHTML = `<strong>Error:</strong> ${message}`;
                            error.style.display = 'block';
                        }

                        function showRepoErrorFromResponse(errorData) {
                            const error = document.getElementById('repo-error');
                            const errorType = errorData.error || 'Error';
                            const message = errorData.message || 'Unknown error occurred';
                            const status = errorData.status || 'Unknown';
                            
                            let errorHtml = `<strong>${errorType}</strong> (Status: ${status})<br>`;
                            errorHtml += `<span style="margin-top: 5px; display: block;">${message}</span>`;
                            
                            error.innerHTML = errorHtml;
                            error.style.display = 'block';
                        }

                        function displayPullRequests(pullRequests) {
                            const results = document.getElementById('repo-results');
                            
                            pullRequests.forEach(pr => {
                                const card = document.createElement('div');
                                card.className = 'pr-card';
                                
                                const createdAt = pr.createdAt ? new Date(pr.createdAt).toLocaleString() : 'N/A';
                                const mergedAt = pr.mergedAt ? new Date(pr.mergedAt).toLocaleString() : null;
                                const state = pr.mergedAt ? 'merged' : (pr.state ? pr.state.toLowerCase() : 'unknown');
                                
                                card.innerHTML = `
                                    <div class="pr-header">
                                        <div>
                                            <span class="pr-number">PR #${pr.number}</span>
                                            <span class="pr-state ${state}">${state.toUpperCase()}</span>
                                        </div>
                                        <span class="event-date">${createdAt}</span>
                                    </div>
                                    <div class="event-body">
                                        <strong>${pr.title || 'No title'}</strong>
                                    </div>
                                    <div class="event-details">
                                        ${pr.user ? `👤 Author: ${pr.user.username || 'Unknown'}` : ''}
                                        ${mergedAt ? `<br>✅ Merged: ${mergedAt}` : ''}
                                        ${pr.htmlUrl ? `<br><a href="${pr.htmlUrl}" target="_blank">🔗 View on GitHub</a>` : ''}
                                    </div>
                                `;
                                
                                results.appendChild(card);
                            });
                        }

                        function displayCommits(commits) {
                            const results = document.getElementById('repo-results');
                            
                            commits.forEach(commit => {
                                const card = document.createElement('div');
                                card.className = 'commit-card';
                                
                                const date = commit.date ? new Date(commit.date).toLocaleString() : 'N/A';
                                const shaShort = commit.sha ? commit.sha.substring(0, 7) : 'N/A';
                                
                                card.innerHTML = `
                                    <div class="commit-header">
                                        <span class="commit-sha">${shaShort}</span>
                                        <span class="event-date">${date}</span>
                                    </div>
                                    <div class="commit-message">
                                        ${commit.message || 'No message'}
                                    </div>
                                    <div class="commit-author">
                                        ${commit.authorName ? `👤 ${commit.authorName}` : ''}
                                        ${commit.htmlUrl ? `<br><a href="${commit.htmlUrl}" target="_blank">🔗 View on GitHub</a>` : ''}
                                    </div>
                                `;
                                
                                results.appendChild(card);
                            });
                        }
                    </script>
                </body>
                </html>
                """;

            byte[] response = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.length);


            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }

    static class RepositoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            
            // Route to appropriate handler based on path
            if (path.endsWith("/pull-requests/merged")) {
                new Handler.RepositoryMergedPullRequestsHandler().handle(exchange);
            } else if (path.endsWith("/pull-requests")) {
                new Handler.RepositoryPullRequestsHandler().handle(exchange);
            } else if (path.endsWith("/commits")) {
                new Handler.RepositoryCommitsHandler().handle(exchange);
            } else {
                // Invalid endpoint
                String error = "{\"error\": \"Invalid repository endpoint\"}";
                byte[] response = error.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(400, response.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
            }
        }
    }
}
