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
                    <title>User Activity Monitor</title>
                    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
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
                        button:hover:not(:disabled) {
                            transform: translateY(-2px);
                        }
                        button:active:not(:disabled) {
                            transform: translateY(0);
                        }
                        button:disabled {
                            background: #ccc;
                            cursor: not-allowed;
                            opacity: 0.6;
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
                        .pr-card, .commit-card, .life-avg-card {
                            background: white;
                            padding: 20px;
                            border-radius: 10px;
                            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
                            transition: transform 0.2s;
                        }
                        .pr-card:hover, .commit-card:hover, .life-avg-card:hover {
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
                        .pagination {
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            gap: 10px;
                            margin-top: 20px;
                            padding: 20px;
                            background: white;
                            border-radius: 10px;
                            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
                        }
                        .pagination button {
                            padding: 10px 20px;
                            font-size: 14px;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            color: white;
                            border: none;
                            border-radius: 5px;
                            cursor: pointer;
                            transition: transform 0.2s;
                        }
                        .pagination button:hover:not(:disabled) {
                            transform: translateY(-2px);
                        }
                        .pagination button:disabled {
                            background: #ccc;
                            cursor: not-allowed;
                            opacity: 0.6;
                        }
                        .pagination-info {
                            color: #333;
                            font-size: 16px;
                            font-weight: 500;
                        }
                        .pagination select {
                            padding: 8px 12px;
                            font-size: 14px;
                            border: 2px solid #e0e0e0;
                            border-radius: 5px;
                            outline: none;
                            cursor: pointer;
                        }
                        .pagination select:focus {
                            border-color: #667eea;
                        }
                        .participation-chart {
                            background: white;
                            padding: 20px;
                            border-radius: 10px;
                            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
                            margin-bottom: 20px;
                        }
                        .participation-chart h3 {
                            margin-bottom: 20px;
                            color: #333;
                            font-size: 1.3em;
                        }
                        .participation-item {
                            margin-bottom: 15px;
                        }
                        .participation-header {
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            margin-bottom: 5px;
                        }
                        .participation-username {
                            font-weight: bold;
                            color: #667eea;
                        }
                        .participation-stats {
                            color: #666;
                            font-size: 14px;
                        }
                        .participation-bar-container {
                            width: 100%;
                            height: 25px;
                            background: #e0e0e0;
                            border-radius: 12px;
                            overflow: hidden;
                        }
                        .participation-bar {
                            height: 100%;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            border-radius: 12px;
                            transition: width 0.5s ease;
                            display: flex;
                            align-items: center;
                            justify-content: flex-end;
                            padding-right: 8px;
                            color: white;
                            font-size: 12px;
                            font-weight: bold;
                        }
                        .life-avg-chart-container {
                            background: white;
                            padding: 30px;
                            border-radius: 10px;
                            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
                            margin-bottom: 20px;
                        }
                        .life-avg-chart-container h3 {
                            margin-bottom: 20px;
                            color: #333;
                            font-size: 1.3em;
                            text-align: center;
                        }
                        .life-avg-chart-wrapper {
                            position: relative;
                            height: 400px;
                            width: 100%;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>📊 User Activity Monitor</h1>
                        <h2 style="color: white; text-align: center; margin-bottom: 20px; font-size: 1.5em; text-shadow: 1px 1px 2px rgba(0,0,0,0.2);">User Events</h2>
                        
                        <div class="search-container">
                            <div class="search-box">
                                <input type="text" id="username" placeholder="Enter username..." value="octocat">
                                <button onclick="fetchEvents()">🔍 Search</button>
                                <button onclick="clearEvents()" style="background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);">🗑️ Clear</button>
                            </div>
                            <div id="loading">Loading...</div>
                            <div id="error"></div>
                        </div>
                        
                        <div id="results"></div>
                        <div id="pagination" class="pagination" style="display: none;">
                            <button id="prevPage" onclick="previousPage()">← Anterior</button>
                            <span class="pagination-info">
                                Página <span id="currentPage">1</span>
                            </span>
                            <select id="perPage" onchange="changePerPage()">
                                <option value="10">10 por página</option>
                                <option value="30" selected>30 por página</option>
                                <option value="50">50 por página</option>
                                <option value="100">100 por página</option>
                            </select>
                            <button id="nextPage" onclick="nextPage()">Siguiente →</button>
                        </div>
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
                                <button onclick="fetchPullRequestsLifeAvg()">📊 PR Life Avg</button>
                                <button onclick="clearRepoResults()" style="background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);">🗑️ Clear</button>
                            </div>
                            <div id="repo-loading">Loading...</div>
                            <div id="repo-error"></div>
                        </div>
                        
                        <div id="repo-results"></div>
                        <div id="repo-pagination" class="pagination" style="display: none;">
                            <button id="repoPrevPage" onclick="repoPreviousPage()">← Anterior</button>
                            <span class="pagination-info">
                                Página <span id="repoCurrentPage">1</span>
                            </span>
                            <select id="repoPerPage" onchange="repoChangePerPage()">
                                <option value="10">10 por página</option>
                                <option value="30" selected>30 por página</option>
                                <option value="50">50 por página</option>
                                <option value="100">100 por página</option>
                            </select>
                            <button id="repoNextPage" onclick="repoNextPage()">Siguiente →</button>
                        </div>
                    </div>

                    <script>
                        // Estado de paginación para eventos de usuario
                        let currentPage = 1;
                        let currentPerPage = 30;
                        let lastEventsCount = 0;
                        
                        // Estado de paginación para repositorio
                        let repoCurrentPage = 1;
                        let repoCurrentPerPage = 30;
                        let lastRepoItemsCount = 0;
                        let currentRepoType = null; // 'pr', 'merged', 'commits', 'life-avg'
                        
                        const usernameInput = document.getElementById('username');
                        
                        usernameInput.addEventListener('keypress', function(e) {
                            if (e.key === 'Enter') {
                                currentPage = 1;
                                fetchEvents();
                            }
                        });

                        async function fetchEvents(page = 1) {
                            const username = usernameInput.value.trim();
                            
                            if (!username) {
                                showError('Please enter a username');
                                return;
                            }

                            currentPage = page;
                            currentPerPage = parseInt(document.getElementById('perPage').value);

                            const loading = document.getElementById('loading');
                            const error = document.getElementById('error');
                            const results = document.getElementById('results');
                            const pagination = document.getElementById('pagination');
                            const searchButton = document.querySelector('button[onclick="fetchEvents()"]');
                            const clearButton = document.querySelector('button[onclick="clearEvents()"]');

                            if (searchButton) searchButton.disabled = true;
                            if (clearButton) clearButton.disabled = true;

                            loading.style.display = 'block';
                            error.style.display = 'none';
                            results.innerHTML = '';

                            try {
                                const response = await fetch(`/api/user/${username}?page=${currentPage}&per_page=${currentPerPage}`);
                                
                                if (!response.ok) {
                                    const errorData = await response.json();
                                    loading.style.display = 'none';
                                    pagination.style.display = 'none';
                                    showErrorFromResponse(errorData);
                                    return;
                                }
                                
                                const events = await response.json();
                                loading.style.display = 'none';
                                lastEventsCount = events.length;
                                
                                if (events.length === 0 && currentPage === 1) {
                                    pagination.style.display = 'none';
                                    showError('No events found for this user');
                                    return;
                                }
                                
                                if (events.length === 0 && currentPage > 1) {
                                    currentPage = 1;
                                    await fetchEvents(1);
                                    return;
                                }
                                
                                displayEvents(events);
                                updatePagination();
                            } catch (err) {
                                loading.style.display = 'none';
                                pagination.style.display = 'none';
                                showError('Failed to fetch events: ' + err.message + '. Make sure the backend is running on localhost:8080');
                            } finally {
                                const searchButton = document.querySelector('button[onclick="fetchEvents()"]');
                                const clearButton = document.querySelector('button[onclick="clearEvents()"]');
                                if (searchButton) searchButton.disabled = false;
                                if (clearButton) clearButton.disabled = false;
                            }
                        }
                        
                        function updatePagination() {
                            const pagination = document.getElementById('pagination');
                            const currentPageSpan = document.getElementById('currentPage');
                            const prevButton = document.getElementById('prevPage');
                            const nextButton = document.getElementById('nextPage');
                            
                            currentPageSpan.textContent = currentPage;
                            prevButton.disabled = currentPage === 1;
                            nextButton.disabled = lastEventsCount < currentPerPage;
                            
                            if (lastEventsCount > 0 || currentPage > 1) {
                                pagination.style.display = 'flex';
                            } else {
                                pagination.style.display = 'none';
                            }
                        }
                        
                        function previousPage() {
                            if (currentPage > 1) {
                                fetchEvents(currentPage - 1);
                            }
                        }
                        
                        function nextPage() {
                            if (lastEventsCount >= currentPerPage) {
                                fetchEvents(currentPage + 1);
                            }
                        }
                        
                        function changePerPage() {
                            currentPage = 1;
                            fetchEvents(1);
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
                            const pagination = document.getElementById('pagination');
                            
                            results.innerHTML = '';
                            error.style.display = 'none';
                            loading.style.display = 'none';
                            pagination.style.display = 'none';
                            currentPage = 1;
                            lastEventsCount = 0;
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
                                        <span class="event-actor">${event.user?.username || 'Unknown'}</span>
                                        performed 
                                        <span class="event-actor">${event.type}</span>
                                        on
                                        <span class="event-repo">${event.repo?.name || 'Unknown'}</span>
                                    </div>
                                    ${details}
                                `;
                                
                                results.appendChild(card);
                            });
                        }

                        // Repository functions
                        async function fetchPullRequests(page = 1) {
                            const owner = document.getElementById('owner').value.trim();
                            const repository = document.getElementById('repository').value.trim();
                            
                            if (!owner || !repository) {
                                showRepoError('Please enter both owner and repository');
                                return;
                            }

                            repoCurrentPage = page;
                            repoCurrentPerPage = parseInt(document.getElementById('repoPerPage').value);
                            currentRepoType = 'pr';

                            const loading = document.getElementById('repo-loading');
                            const error = document.getElementById('repo-error');
                            const results = document.getElementById('repo-results');
                            const pagination = document.getElementById('repo-pagination');
                            const buttons = document.querySelectorAll('.button-group button');
                            buttons.forEach(btn => btn.disabled = true);

                            loading.style.display = 'block';
                            error.style.display = 'none';
                            results.innerHTML = '';

                            try {
                                const response = await fetch(`/api/repository/${owner}/${repository}/pull-requests?page=${repoCurrentPage}&per_page=${repoCurrentPerPage}`);
                                
                                if (!response.ok) {
                                    const errorData = await response.json();
                                    loading.style.display = 'none';
                                    pagination.style.display = 'none';
                                    showRepoErrorFromResponse(errorData);
                                    return;
                                }
                                
                                const pullRequests = await response.json();
                                loading.style.display = 'none';
                                lastRepoItemsCount = pullRequests.length;
                                
                                if (pullRequests.length === 0 && repoCurrentPage === 1) {
                                    pagination.style.display = 'none';
                                    showRepoError('No pull requests found for this repository');
                                    return;
                                }
                                
                                if (pullRequests.length === 0 && repoCurrentPage > 1) {
                                    repoCurrentPage = 1;
                                    await fetchPullRequests(1);
                                    return;
                                }
                                
                                displayPullRequests(pullRequests);
                                updateRepoPagination();
                            } catch (err) {
                                loading.style.display = 'none';
                                pagination.style.display = 'none';
                                showRepoError('Failed to fetch pull requests: ' + err.message + '. Make sure the backend is running on localhost:8080');
                            } finally {
                                const buttons = document.querySelectorAll('.button-group button');
                                buttons.forEach(btn => btn.disabled = false);
                            }
                        }

                        async function fetchMergedPullRequests(page = 1) {
                            const owner = document.getElementById('owner').value.trim();
                            const repository = document.getElementById('repository').value.trim();
                            
                            if (!owner || !repository) {
                                showRepoError('Please enter both owner and repository');
                                return;
                            }

                            repoCurrentPage = page;
                            repoCurrentPerPage = parseInt(document.getElementById('repoPerPage').value);
                            currentRepoType = 'merged';

                            const loading = document.getElementById('repo-loading');
                            const error = document.getElementById('repo-error');
                            const results = document.getElementById('repo-results');
                            const pagination = document.getElementById('repo-pagination');
                            const buttons = document.querySelectorAll('.button-group button');
                            buttons.forEach(btn => btn.disabled = true);

                            loading.style.display = 'block';
                            error.style.display = 'none';
                            results.innerHTML = '';

                            try {
                                const response = await fetch(`/api/repository/${owner}/${repository}/pull-requests/merged?page=${repoCurrentPage}&per_page=${repoCurrentPerPage}`);
                                
                                if (!response.ok) {
                                    const errorData = await response.json();
                                    loading.style.display = 'none';
                                    pagination.style.display = 'none';
                                    showRepoErrorFromResponse(errorData);
                                    return;
                                }
                                
                                const pullRequests = await response.json();
                                loading.style.display = 'none';
                                lastRepoItemsCount = pullRequests.length;
                                
                                if (pullRequests.length === 0 && repoCurrentPage === 1) {
                                    pagination.style.display = 'none';
                                    showRepoError('No merged pull requests found for this repository');
                                    return;
                                }
                                
                                if (pullRequests.length === 0 && repoCurrentPage > 1) {
                                    repoCurrentPage = 1;
                                    await fetchMergedPullRequests(1);
                                    return;
                                }
                                
                                displayPullRequests(pullRequests);
                                updateRepoPagination();
                            } catch (err) {
                                loading.style.display = 'none';
                                pagination.style.display = 'none';
                                showRepoError('Failed to fetch merged pull requests: ' + err.message + '. Make sure the backend is running on localhost:8080');
                            } finally {
                                const buttons = document.querySelectorAll('.button-group button');
                                buttons.forEach(btn => btn.disabled = false);
                            }
                        }

                        async function fetchCommits(page = 1) {
                            const owner = document.getElementById('owner').value.trim();
                            const repository = document.getElementById('repository').value.trim();
                            
                            if (!owner || !repository) {
                                showRepoError('Please enter both owner and repository');
                                return;
                            }

                            repoCurrentPage = page;
                            repoCurrentPerPage = parseInt(document.getElementById('repoPerPage').value);
                            currentRepoType = 'commits';

                            const loading = document.getElementById('repo-loading');
                            const error = document.getElementById('repo-error');
                            const results = document.getElementById('repo-results');
                            const pagination = document.getElementById('repo-pagination');
                            const buttons = document.querySelectorAll('.button-group button');
                            buttons.forEach(btn => btn.disabled = true);

                            loading.style.display = 'block';
                            error.style.display = 'none';
                            results.innerHTML = '';

                            try {
                                const response = await fetch(`/api/repository/${owner}/${repository}/commits?page=${repoCurrentPage}&per_page=${repoCurrentPerPage}`);
                                
                                if (!response.ok) {
                                    const errorData = await response.json();
                                    loading.style.display = 'none';
                                    pagination.style.display = 'none';
                                    showRepoErrorFromResponse(errorData);
                                    return;
                                }
                                
                                const commitsResponse = await response.json();
                                loading.style.display = 'none';
                                
                                const commits = commitsResponse.commits || [];
                                const userParticipations = commitsResponse.userParticipations || [];
                                
                                lastRepoItemsCount = commits.length;
                                
                                if (commits.length === 0 && repoCurrentPage === 1) {
                                    pagination.style.display = 'none';
                                    showRepoError('No commits found for this repository');
                                    return;
                                }
                                
                                if (commits.length === 0 && repoCurrentPage > 1) {
                                    repoCurrentPage = 1;
                                    await fetchCommits(1);
                                    return;
                                }
                                
                                displayCommitsWithParticipation(commits, userParticipations);
                                updateRepoPagination();
                            } catch (err) {
                                loading.style.display = 'none';
                                pagination.style.display = 'none';
                                showRepoError('Failed to fetch commits: ' + err.message + '. Make sure the backend is running on localhost:8080');
                            } finally {
                                const buttons = document.querySelectorAll('.button-group button');
                                buttons.forEach(btn => btn.disabled = false);
                            }
                        }

                        async function fetchPullRequestsLifeAvg() {
                            const owner = document.getElementById('owner').value.trim();
                            const repository = document.getElementById('repository').value.trim();
                            
                            if (!owner || !repository) {
                                showRepoError('Please enter both owner and repository');
                                return;
                            }

                            currentRepoType = 'life-avg';

                            const loading = document.getElementById('repo-loading');
                            const error = document.getElementById('repo-error');
                            const results = document.getElementById('repo-results');
                            const pagination = document.getElementById('repo-pagination');
                            const buttons = document.querySelectorAll('.button-group button');
                            buttons.forEach(btn => btn.disabled = true);

                            loading.style.display = 'block';
                            error.style.display = 'none';
                            results.innerHTML = '';
                            pagination.style.display = 'none';

                            try {
                                const response = await fetch(`/api/repository/${owner}/${repository}/pull-requests/life-avg`);
                                
                                if (!response.ok) {
                                    const errorData = await response.json();
                                    loading.style.display = 'none';
                                    showRepoErrorFromResponse(errorData);
                                    return;
                                }
                                
                                const lifeAvgData = await response.json();
                                loading.style.display = 'none';
                                
                                if (lifeAvgData.length === 0) {
                                    showRepoError('No pull request life average data found for this repository');
                                    return;
                                }
                                
                                displayPullRequestsLifeAvg(lifeAvgData);
                            } catch (err) {
                                loading.style.display = 'none';
                                showRepoError('Failed to fetch pull requests life average: ' + err.message + '. Make sure the backend is running on localhost:8080');
                            } finally {
                                const buttons = document.querySelectorAll('.button-group button');
                                buttons.forEach(btn => btn.disabled = false);
                            }
                        }
                        
                        function updateRepoPagination() {
                            const pagination = document.getElementById('repo-pagination');
                            const currentPageSpan = document.getElementById('repoCurrentPage');
                            const prevButton = document.getElementById('repoPrevPage');
                            const nextButton = document.getElementById('repoNextPage');
                            
                            currentPageSpan.textContent = repoCurrentPage;
                            prevButton.disabled = repoCurrentPage === 1;
                            nextButton.disabled = lastRepoItemsCount < repoCurrentPerPage;
                            
                            if (lastRepoItemsCount > 0 || repoCurrentPage > 1) {
                                pagination.style.display = 'flex';
                            } else {
                                pagination.style.display = 'none';
                            }
                        }
                        
                        function repoPreviousPage() {
                            if (repoCurrentPage > 1) {
                                if (currentRepoType === 'pr') {
                                    fetchPullRequests(repoCurrentPage - 1);
                                } else if (currentRepoType === 'merged') {
                                    fetchMergedPullRequests(repoCurrentPage - 1);
                                } else if (currentRepoType === 'commits') {
                                    fetchCommits(repoCurrentPage - 1);
                                }
                            }
                        }
                        
                        function repoNextPage() {
                            if (lastRepoItemsCount >= repoCurrentPerPage) {
                                if (currentRepoType === 'pr') {
                                    fetchPullRequests(repoCurrentPage + 1);
                                } else if (currentRepoType === 'merged') {
                                    fetchMergedPullRequests(repoCurrentPage + 1);
                                } else if (currentRepoType === 'commits') {
                                    fetchCommits(repoCurrentPage + 1);
                                }
                            }
                        }
                        
                        function repoChangePerPage() {
                            repoCurrentPage = 1;
                            if (currentRepoType === 'pr') {
                                fetchPullRequests(1);
                            } else if (currentRepoType === 'merged') {
                                fetchMergedPullRequests(1);
                            } else if (currentRepoType === 'commits') {
                                fetchCommits(1);
                            }
                        }

                        function clearRepoResults() {
                            const results = document.getElementById('repo-results');
                            const error = document.getElementById('repo-error');
                            const loading = document.getElementById('repo-loading');
                            const pagination = document.getElementById('repo-pagination');
                            
                            results.innerHTML = '';
                            error.style.display = 'none';
                            loading.style.display = 'none';
                            pagination.style.display = 'none';
                            repoCurrentPage = 1;
                            lastRepoItemsCount = 0;
                            currentRepoType = null;
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
                                        ${pr.htmlUrl ? `<br><a href="${pr.htmlUrl}" target="_blank">🔗 View online</a>` : ''}
                                    </div>
                                `;
                                
                                results.appendChild(card);
                            });
                        }

                        function displayCommitsWithParticipation(commits, userParticipations) {
                            const results = document.getElementById('repo-results');
                            results.innerHTML = '';
                            
                            // Mostrar gráfico de participación si hay datos
                            if (userParticipations && userParticipations.length > 0) {
                                const chartCard = document.createElement('div');
                                chartCard.className = 'participation-chart';
                                
                                let chartHTML = '<h3>📊 Participación de Usuarios en Commits</h3>';
                                
                                userParticipations.forEach(participation => {
                                    chartHTML += `
                                        <div class="participation-item">
                                            <div class="participation-header">
                                                <span class="participation-username">👤 ${participation.username || 'Unknown'}</span>
                                                <span class="participation-stats">${participation.commitCount} commits (${participation.percentage.toFixed(2)}%)</span>
                                            </div>
                                            <div class="participation-bar-container">
                                                <div class="participation-bar" style="width: ${participation.percentage}%">
                                                    ${participation.percentage >= 5 ? participation.percentage.toFixed(1) + '%' : ''}
                                                </div>
                                            </div>
                                        </div>
                                    `;
                                });
                                
                                chartCard.innerHTML = chartHTML;
                                results.appendChild(chartCard);
                            }
                            
                            // Mostrar commits
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
                                        ${commit.htmlUrl ? `<br><a href="${commit.htmlUrl}" target="_blank">🔗 View online</a>` : ''}
                                    </div>
                                `;
                                
                                results.appendChild(card);
                            });
                        }
                        
                        function displayCommits(commits) {
                            displayCommitsWithParticipation(commits, []);
                        }

                        function displayPullRequestsLifeAvg(lifeAvgData) {
                            const results = document.getElementById('repo-results');
                            results.innerHTML = '';
                            
                            if (!lifeAvgData || lifeAvgData.length === 0) {
                                return;
                            }
                            
                            // Ordenar datos por mes para asegurar orden cronológico
                            const sortedData = [...lifeAvgData].sort((a, b) => {
                                if (a.month < b.month) return -1;
                                if (a.month > b.month) return 1;
                                return 0;
                            });
                            
                            // Convertir duraciones ISO-8601 a horas para el gráfico
                            const parseDuration = (durationStr) => {
                                if (!durationStr) return 0;
                                
                                const str = String(durationStr);
                                let totalHours = 0;
                                
                                // Capturar horas (formato: PT56H o 56H)
                                const hoursMatch = str.match(/(\\d+)H/);
                                if (hoursMatch) {
                                    totalHours += parseInt(hoursMatch[1], 10);
                                }
                                
                                // Capturar minutos (formato: 1M, debe estar entre H y S o al final antes de S)
                                // Buscar M que no sea seguido inmediatamente por S (para distinguir de MS)
                                const minutesMatch = str.match(/(\\d+)M(?!S)/);
                                if (minutesMatch) {
                                    totalHours += parseInt(minutesMatch[1], 10) / 60;
                                }
                                
                                // Capturar segundos (pueden tener decimales, formato: 34.956521739S)
                                const secondsMatch = str.match(/(\\d+(?:\\.\\d+)?)S/);
                                if (secondsMatch) {
                                    totalHours += parseFloat(secondsMatch[1]) / 3600;
                                }
                                
                                return totalHours;
                            };
                            
                            // Preparar datos para el gráfico
                            const labels = sortedData.map(item => {
                                // Formatear mes de "YYYY-MM" a "MMM YYYY" (ej: "2024-01" -> "Ene 2024")
                                const [year, month] = item.month.split('-');
                                const monthNames = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 
                                                   'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
                                const monthIndex = parseInt(month) - 1;
                                return `${monthNames[monthIndex]} ${year}`;
                            });
                            
                            const hoursData = sortedData.map(item => {
                                const totalHours = parseDuration(item.hours);
                                return parseFloat(totalHours.toFixed(2));
                            });
                            
                            const counts = sortedData.map(item => item.count || 0);
                            
                            // Crear contenedor del gráfico
                            const chartContainer = document.createElement('div');
                            chartContainer.className = 'life-avg-chart-container';
                            
                            chartContainer.innerHTML = `
                                <h3>📊 Promedio de Vida de Pull Requests a lo Largo del Tiempo</h3>
                                <div class="life-avg-chart-wrapper">
                                    <canvas id="lifeAvgChart"></canvas>
                                </div>
                            `;
                            
                            results.appendChild(chartContainer);
                            
                            // Crear el gráfico de barras
                            const ctx = document.getElementById('lifeAvgChart').getContext('2d');
                            
                            new Chart(ctx, {
                                type: 'bar',
                                data: {
                                    labels: labels,
                                    datasets: [{
                                        label: 'Promedio de Vida (horas)',
                                        data: hoursData,
                                        backgroundColor: 'rgba(102, 126, 234, 0.8)',
                                        borderColor: 'rgba(102, 126, 234, 1)',
                                        borderWidth: 2,
                                        borderRadius: 8,
                                        borderSkipped: false,
                                    }]
                                },
                                options: {
                                    responsive: true,
                                    maintainAspectRatio: false,
                                    plugins: {
                                        legend: {
                                            display: true,
                                            position: 'top',
                                            labels: {
                                                font: {
                                                    size: 14,
                                                    weight: 'bold'
                                                },
                                                color: '#333'
                                            }
                                        },
                                        tooltip: {
                                            callbacks: {
                                                label: function(context) {
                                                    const hours = context.parsed.y;
                                                    const index = context.dataIndex;
                                                    const count = counts[index];
                                                    
                                                    let formattedTime = '';
                                                    if (hours >= 24) {
                                                        const days = Math.floor(hours / 24);
                                                        const remainingHours = Math.floor(hours % 24);
                                                        formattedTime = `${days}d ${remainingHours}h`;
                                                    } else {
                                                        formattedTime = `${hours.toFixed(2)}h`;
                                                    }
                                                    
                                                    return [
                                                        `Promedio: ${formattedTime}`,
                                                        `PRs cerrados: ${count}`
                                                    ];
                                                }
                                            },
                                            backgroundColor: 'rgba(0, 0, 0, 0.8)',
                                            padding: 12,
                                            titleFont: {
                                                size: 14,
                                                weight: 'bold'
                                            },
                                            bodyFont: {
                                                size: 13
                                            }
                                        }
                                    },
                                    scales: {
                                        y: {
                                            beginAtZero: true,
                                            title: {
                                                display: true,
                                                text: 'Promedio de Vida (horas)',
                                                font: {
                                                    size: 14,
                                                    weight: 'bold'
                                                },
                                                color: '#333'
                                            },
                                            ticks: {
                                                font: {
                                                    size: 12
                                                },
                                                color: '#666',
                                                callback: function(value) {
                                                    if (value >= 24) {
                                                        const days = Math.floor(value / 24);
                                                        const hours = Math.floor(value % 24);
                                                        return days > 0 ? `${days}d ${hours}h` : `${hours}h`;
                                                    }
                                                    return value + 'h';
                                                }
                                            },
                                            grid: {
                                                color: 'rgba(0, 0, 0, 0.1)'
                                            }
                                        },
                                        x: {
                                            title: {
                                                display: true,
                                                text: 'Mes',
                                                font: {
                                                    size: 14,
                                                    weight: 'bold'
                                                },
                                                color: '#333'
                                            },
                                            ticks: {
                                                font: {
                                                    size: 12
                                                },
                                                color: '#666',
                                                maxRotation: 45,
                                                minRotation: 45
                                            },
                                            grid: {
                                                display: false
                                            }
                                        }
                                    },
                                    animation: {
                                        duration: 1000,
                                        easing: 'easeInOutQuart'
                                    }
                                }
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
            if (path.endsWith("/pull-requests/life-avg")) {
                new Handler.RepositoryPullRequestsLifeAvgHandler().handle(exchange);
            } else if (path.endsWith("/pull-requests/merged")) {
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
