package integration;

import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.models.CommitsResponse;
import edu.itba.useractivity.domain.ports.inbound.RepositoryInboundPort;
import edu.itba.useractivity.infrastructure.adapters.driving.RepositoryController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RepositoryController.class)
class RepositoryControllerIT {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RepositoryInboundPort repositoryInboundPort;

    @Test
    @DisplayName("GET /repository/{owner}/{repo}/pull-requests responde 200 (lista vacía OK)")
    void getPullRequests_ok_empty() throws Exception {
        Mockito.when(repositoryInboundPort.getPullRequests(eq("itba"), eq("repo"), eq(1), eq(30)))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/repository/itba/repo/pull-requests")
                        .param("page", "1")
                        .param("per_page", "30"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /repository/{owner}/{repo}/pull-requests/merged responde 200 (lista vacía OK)")
    void getMergedPullRequests_ok_empty() throws Exception {
        Mockito.when(repositoryInboundPort.getMergedPullRequests(eq("itba"), eq("repo"), eq(1), eq(30)))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/repository/itba/repo/pull-requests/merged")
                        .param("page", "1")
                        .param("per_page", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /repository/{owner}/{repo}/commits devuelve commits (usa tu modelo con 5 campos)")
    void getCommits_ok() throws Exception {
        Commit c = new Commit(
                "sha-1", "msg", "author",
                ZonedDateTime.parse("2025-11-08T12:00:00Z"),
                "https://commit/sha-1"
        );
        Mockito.when(repositoryInboundPort.getCommits(eq("itba"), eq("repo"), eq(1), eq(30)))
                .thenReturn(new CommitsResponse(
                        List.of(c),
                        List.of()
                ));

        mvc.perform(get("/repository/itba/repo/commits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sha", is("sha-1")))
                .andExpect(jsonPath("$[0].authorName", is("author")))
                .andExpect(jsonPath("$[0].htmlUrl", is("https://commit/sha-1")));
    }

    @Test
    @DisplayName("GET /repository/{owner}/{repo}/pull-requests/life-avg responde 200 (lista vacía OK)")
    void getLifeAvg_ok_empty() throws Exception {
        Mockito.when(repositoryInboundPort.getPullRequestsLifeAvg(eq("itba"), eq("repo")))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/repository/itba/repo/pull-requests/life-avg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
