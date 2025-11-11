package infraestructure.adapters.driven.github;

import edu.itba.useractivity.domain.enums.EventType;
import edu.itba.useractivity.infrastructure.adapters.driven.github.GitHubEventAdapter;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.ExternalServiceException;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.GitHubServerException;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.RateLimitExceededException;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.ResourceNotFoundException;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.GitHubClientException;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.GitHubApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios del adapter SIN tocar la red:
 * mockeamos HttpClient.newHttpClient() para que devuelva nuestro mock.
 */
class GitHubEventAdapterTest {

    private HttpClient mockHttpClient() {
        return mock(HttpClient.class);
    }

    @SuppressWarnings("unchecked")
    private HttpResponse<String> mockResponse() {
        return (HttpResponse<String>) mock(HttpResponse.class);
    }

    @Test
    @DisplayName("200 OK: mapea un PushEvent correctamente")
    void getEventsByUser_ok() throws Exception {
        String jsonResponse = """
        [
          {
            "id": "e1",
            "type": "PushEvent",
            "actor": { "id": 1, "login": "user1", "avatar_url": "a", "html_url": "p", "type": "User" },
            "repo": { "id": 2, "name": "repo1", "full_name": "user1/repo1", "html_url": "h", "description": "d", "private": false },
            "created_at": "2025-01-01T00:00:00Z",
            "payload": {
              "ref": "refs/heads/main",
              "before": "abc123",
              "head": "def456",
              "commits": [
                { "sha": "def456",
                  "commit": { "message": "Initial commit", "author": { "name": "user1", "date": "2025-01-01T00:00:00Z" } },
                  "html_url": "https://example.com/commit/def456" }
              ]
            }
          }
        ]
        """;

        HttpClient http = mockHttpClient();
        HttpResponse<String> resp = mockResponse();
        when(resp.statusCode()).thenReturn(200);
        when(resp.body()).thenReturn(jsonResponse);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubEventAdapter adapter = new GitHubEventAdapter();
            var events = adapter.getEventsByUser("testuser", 1, 10);

            assertThat(events).hasSize(1);
            assertThat(events.get(0).getId()).isEqualTo("e1");
            assertThat(events.get(0).getType()).isEqualTo(EventType.PUSH);
        }
    }

    @Test
    @DisplayName("200 OK: respuesta vacía devuelve lista vacía")
    void getEventsByUser_emptyResponse() throws Exception {
        HttpClient http = mockHttpClient();
        HttpResponse<String> resp = mockResponse();
        when(resp.statusCode()).thenReturn(200);
        when(resp.body()).thenReturn("[]");
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubEventAdapter adapter = new GitHubEventAdapter();
            var events = adapter.getEventsByUser("testuser", 1, 10);

            assertThat(events).isEmpty();
        }
    }

    @Test
    @DisplayName("404 Not Found: lanza ResourceNotFoundException")
    void getEventsByUser_404() throws Exception {
        HttpClient http = mockHttpClient();
        HttpResponse<String> resp = mockResponse();
        when(resp.statusCode()).thenReturn(404);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubEventAdapter adapter = new GitHubEventAdapter();
            assertThatThrownBy(() -> adapter.getEventsByUser("missing", 1, 10))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Test
    @DisplayName("403 Forbidden: lanza RateLimitExceededException")
    void getEventsByUser_403() throws Exception {
        HttpClient http = mockHttpClient();
        HttpResponse<String> resp = mockResponse();
        when(resp.statusCode()).thenReturn(403);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubEventAdapter adapter = new GitHubEventAdapter();
            assertThatThrownBy(() -> adapter.getEventsByUser("testuser", 1, 10))
                    .isInstanceOf(RateLimitExceededException.class);
        }
    }

    @Test
    @DisplayName("401 Unauthorized: lanza GitHubClientException")
    void getEventsByUser_401() throws Exception {
        HttpClient http = mockHttpClient();
        HttpResponse<String> resp = mockResponse();
        when(resp.statusCode()).thenReturn(401);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubEventAdapter adapter = new GitHubEventAdapter();
            assertThatThrownBy(() -> adapter.getEventsByUser("unauthorized", 1, 10))
                    .isInstanceOf(GitHubClientException.class);
        }
    }

    @Test
    @DisplayName(">=500 server error: lanza GitHubServerException")
    void getEventsByUser_500() throws Exception {
        HttpClient http = mockHttpClient();
        HttpResponse<String> resp = mockResponse();
        when(resp.statusCode()).thenReturn(500);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubEventAdapter adapter = new GitHubEventAdapter();
            assertThatThrownBy(() -> adapter.getEventsByUser("testuser", 1, 10))
                    .isInstanceOf(GitHubServerException.class);
        }
    }

    @Test
    @DisplayName("Unhandled status (418): lanza GitHubApiException")
    void getEventsByUser_unhandledStatus() throws Exception {
        HttpClient http = mockHttpClient();
        HttpResponse<String> resp = mockResponse();
        when(resp.statusCode()).thenReturn(418); // Teapot 😉
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubEventAdapter adapter = new GitHubEventAdapter();
            assertThatThrownBy(() -> adapter.getEventsByUser("testuser", 1, 10))
                    .isInstanceOf(GitHubApiException.class);
        }
    }

    @Test
    @DisplayName("IOException: setea interrupt y relanza ExternalServiceException")
    void getEventsByUser_ioException() throws Exception {
        HttpClient http = mockHttpClient();
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("boom"));

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubEventAdapter adapter = new GitHubEventAdapter();

            assertThatThrownBy(() -> adapter.getEventsByUser("testuser", 1, 10))
                    .isInstanceOf(ExternalServiceException.class);
            Thread.interrupted(); // limpiar flag
        }
    }

    @Test
    @DisplayName("InterruptedException: setea interrupt y relanza ExternalServiceException")
    void getEventsByUser_interrupted() throws Exception {
        HttpClient http = mockHttpClient();
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("int"));

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubEventAdapter adapter = new GitHubEventAdapter();
            try {
                adapter.getEventsByUser("testuser", 1, 10);
            } catch (ExternalServiceException e) {
                assertTrue(Thread.currentThread().isInterrupted(), "El flag de interrupción debería estar seteado");
                Thread.interrupted();
            }
        }
    }

    @Test
    @DisplayName("400 Bad Request: lanza GitHubClientException")
    void getEventsByUser_400() throws Exception {
        HttpClient http = mockHttpClient();
        HttpResponse<String> resp = mockResponse();
        when(resp.statusCode()).thenReturn(400);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubEventAdapter adapter = new GitHubEventAdapter();
            assertThatThrownBy(() -> adapter.getEventsByUser("u", 1, 10))
                    .isInstanceOf(edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.GitHubClientException.class);
        }
    }

    @Test
    @DisplayName("422 Unprocessable Entity: lanza GitHubClientException")
    void getEventsByUser_422() throws Exception {
        HttpClient http = mockHttpClient();
        HttpResponse<String> resp = mockResponse();
        when(resp.statusCode()).thenReturn(422);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubEventAdapter adapter = new GitHubEventAdapter();
            assertThatThrownBy(() -> adapter.getEventsByUser("u", 1, 10))
                    .isInstanceOf(edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.GitHubClientException.class);
        }
    }

}
