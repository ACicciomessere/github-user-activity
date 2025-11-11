package infraestructure.adapters.driven.github;

import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.infrastructure.adapters.driven.github.GitHubRepositoryAdapter;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.GitHubClientException;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.GitHubServerException;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 100% coverage para GitHubRepositoryAdapter:
 * - getPullRequests: 200, 404, 4xx, 5xx, IOException
 * - getCommits:      200, 404, 4xx, 5xx, InterruptedException
 */
class GitHubRepositoryAdapterTest {

    private HttpClient mockHttp() {
        return mock(HttpClient.class);
    }

    @SuppressWarnings("unchecked")
    private HttpResponse<String> mockResp() {
        return (HttpResponse<String>) mock(HttpResponse.class);
    }

    // ---------- getPullRequests ----------

    @Test
    @DisplayName("getPullRequests 200 OK: mapea lista de PRs")
    void getPullRequests_ok() throws Exception {
        String body = """
        [
          {
            "id": 11, "number": 101, "title": "A", "state":"open",
            "user": { "id":1, "login":"u1", "avatar_url":"", "html_url":"", "type":"User" },
            "created_at":"2025-03-01T10:00:00Z", "updated_at":"2025-03-01T10:10:00Z",
            "closed_at": null, "merged_at": null, "html_url":"h1"
          }
        ]
        """;

        HttpClient http = mockHttp();
        HttpResponse<String> resp = mockResp();
        when(resp.statusCode()).thenReturn(200);
        when(resp.body()).thenReturn(body);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubRepositoryAdapter adapter = new GitHubRepositoryAdapter();
            List<PullRequest> prs = adapter.getPullRequests("owner", "repo", 1, 10);

            assertThat(prs).hasSize(1);
            assertThat(prs.get(0).getNumber()).isEqualTo(101);
            assertThat(prs.get(0).isMerged()).isFalse();
        }
    }

    @Test
    @DisplayName("getPullRequests 404: ResourceNotFoundException")
    void getPullRequests_404() throws Exception {
        HttpClient http = mockHttp();
        HttpResponse<String> resp = mockResp();
        when(resp.statusCode()).thenReturn(404);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubRepositoryAdapter adapter = new GitHubRepositoryAdapter();
            assertThatThrownBy(() -> adapter.getPullRequests("owner", "repo", 1, 10))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Test
    @DisplayName("getPullRequests 4xx (≠404): GitHubClientException")
    void getPullRequests_4xx() throws Exception {
        HttpClient http = mockHttp();
        HttpResponse<String> resp = mockResp();
        when(resp.statusCode()).thenReturn(401);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubRepositoryAdapter adapter = new GitHubRepositoryAdapter();
            assertThatThrownBy(() -> adapter.getPullRequests("owner", "repo", 1, 10))
                    .isInstanceOf(GitHubClientException.class);
        }
    }

    @Test
    @DisplayName("getPullRequests 5xx: GitHubServerException")
    void getPullRequests_5xx() throws Exception {
        HttpClient http = mockHttp();
        HttpResponse<String> resp = mockResp();
        when(resp.statusCode()).thenReturn(503);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubRepositoryAdapter adapter = new GitHubRepositoryAdapter();
            assertThatThrownBy(() -> adapter.getPullRequests("owner", "repo", 1, 10))
                    .isInstanceOf(GitHubServerException.class);
        }
    }

    @Test
    @DisplayName("getPullRequests IOException: RuntimeException")
    void getPullRequests_ioException() throws Exception {
        HttpClient http = mockHttp();
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("boom"));

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubRepositoryAdapter adapter = new GitHubRepositoryAdapter();
            assertThatThrownBy(() -> adapter.getPullRequests("owner", "repo", 1, 10))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to fetch pullRequests from GitHub");
        }
    }

    // ---------- getCommits ----------

    @Test
    @DisplayName("getCommits 200 OK: mapea lista de commits")
    void getCommits_ok() throws Exception {
        String body = """
        [
          {
            "sha": "1",
            "commit": {"message":"m1","author":{"name":"A","date":"2025-01-01T00:00:01Z"}},
            "html_url":"u1"
          },
          {
            "sha": "2",
            "commit": {"message":"m2","author":{"name":"B","date":"2025-01-01T00:00:02Z"}},
            "html_url":"u2"
          }
        ]
        """;

        HttpClient http = mockHttp();
        HttpResponse<String> resp = mockResp();
        when(resp.statusCode()).thenReturn(200);
        when(resp.body()).thenReturn(body);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubRepositoryAdapter adapter = new GitHubRepositoryAdapter();
            List<Commit> commits = adapter.getCommits("owner", "repo", 1, 10);

            assertThat(commits).hasSize(2);
            assertThat(commits.get(0).sha()).isEqualTo("1");
            assertThat(commits.get(1).message()).isEqualTo("m2");
        }
    }

    @Test
    @DisplayName("getCommits 404: ResourceNotFoundException")
    void getCommits_404() throws Exception {
        HttpClient http = mockHttp();
        HttpResponse<String> resp = mockResp();
        when(resp.statusCode()).thenReturn(404);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubRepositoryAdapter adapter = new GitHubRepositoryAdapter();
            assertThatThrownBy(() -> adapter.getCommits("owner", "repo", 1, 10))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Test
    @DisplayName("getCommits 4xx (≠404): GitHubClientException")
    void getCommits_4xx() throws Exception {
        HttpClient http = mockHttp();
        HttpResponse<String> resp = mockResp();
        when(resp.statusCode()).thenReturn(400);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubRepositoryAdapter adapter = new GitHubRepositoryAdapter();
            assertThatThrownBy(() -> adapter.getCommits("owner", "repo", 1, 10))
                    .isInstanceOf(GitHubClientException.class);
        }
    }

    @Test
    @DisplayName("getCommits 5xx: GitHubServerException")
    void getCommits_5xx() throws Exception {
        HttpClient http = mockHttp();
        HttpResponse<String> resp = mockResp();
        when(resp.statusCode()).thenReturn(500);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubRepositoryAdapter adapter = new GitHubRepositoryAdapter();
            assertThatThrownBy(() -> adapter.getCommits("owner", "repo", 1, 10))
                    .isInstanceOf(GitHubServerException.class);
        }
    }

    @Test
    @DisplayName("getCommits InterruptedException: RuntimeException")
    void getCommits_interruptedException() throws Exception {
        HttpClient http = mockHttp();
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("int"));

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubRepositoryAdapter adapter = new GitHubRepositoryAdapter();
            assertThatThrownBy(() -> adapter.getCommits("owner", "repo", 1, 10))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to fetch pullRequests from GitHub"); // mismo mensaje en catch
        }
    }

    @Test
    @DisplayName("getPullRequests InterruptedException: RuntimeException")
    void getPullRequests_interruptedException() throws Exception {
        HttpClient http = mock(HttpClient.class);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("int"));

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubRepositoryAdapter adapter = new GitHubRepositoryAdapter();
            assertThatThrownBy(() -> adapter.getPullRequests("owner", "repo", 1, 10))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to fetch pullRequests from GitHub");
        }
    }

    @Test
    @DisplayName("getCommits IOException: RuntimeException")
    void getCommits_ioException() throws Exception {
        HttpClient http = mock(HttpClient.class);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("boom"));

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubRepositoryAdapter adapter = new GitHubRepositoryAdapter();
            assertThatThrownBy(() -> adapter.getCommits("owner", "repo", 1, 10))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to fetch pullRequests from GitHub");
        }
    }

    @Test
    @DisplayName("getPullRequests 403 Forbidden: GitHubClientException (label FORBIDDEN)")
    void getPullRequests_403_forbidden() throws Exception {
        HttpClient http = mock(HttpClient.class);
        @SuppressWarnings("unchecked")
        HttpResponse<String> resp = (HttpResponse<String>) mock(HttpResponse.class);

        when(resp.statusCode()).thenReturn(403);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubRepositoryAdapter adapter = new GitHubRepositoryAdapter();
            assertThatThrownBy(() -> adapter.getPullRequests("owner", "repo", 1, 10))
                    .isInstanceOf(GitHubClientException.class);
        }
    }

    @Test
    @DisplayName("getCommits 422 Unprocessable Entity: GitHubClientException (label UNPROCESSABLE_ENTITY)")
    void getCommits_422_unprocessableEntity() throws Exception {
        HttpClient http = mock(HttpClient.class);
        @SuppressWarnings("unchecked")
        HttpResponse<String> resp = (HttpResponse<String>) mock(HttpResponse.class);

        when(resp.statusCode()).thenReturn(422);
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubRepositoryAdapter adapter = new GitHubRepositoryAdapter();
            assertThatThrownBy(() -> adapter.getCommits("owner", "repo", 1, 10))
                    .isInstanceOf(GitHubClientException.class);
        }
    }

    @Test
    @DisplayName("getPullRequests estado 4xx no mapeado (418): GitHubClientException con mensaje 'Unhandled status code'")
    void getPullRequests_unhandled418() throws Exception {
        HttpClient http = mock(HttpClient.class);
        @SuppressWarnings("unchecked")
        HttpResponse<String> resp = (HttpResponse<String>) mock(HttpResponse.class);

        when(resp.statusCode()).thenReturn(418); // no mapeado y no es 5xx
        when(http.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(resp);

        try (MockedStatic<HttpClient> mocked = Mockito.mockStatic(HttpClient.class)) {
            mocked.when(HttpClient::newHttpClient).thenReturn(http);

            GitHubRepositoryAdapter adapter = new GitHubRepositoryAdapter();
            assertThatThrownBy(() -> adapter.getPullRequests("owner", "repo", 1, 10))
                    .isInstanceOf(GitHubClientException.class)
                    .hasMessageContaining("Unhandled status code");
        }
    }


}
