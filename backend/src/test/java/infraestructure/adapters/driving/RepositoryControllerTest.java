package infraestructure.adapters.driving;

import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.models.PullRequestsLifeAvg;
import edu.itba.useractivity.domain.ports.inbound.RepositoryInboundPort;
import edu.itba.useractivity.infrastructure.adapters.driving.RepositoryController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RepositoryControllerTest {

    @Test
    @DisplayName("@ModelAttribute setRepositoryContext setea owner y repository; GET /pull-requests delega ok")
    void getRepositoryPullRequests_ok() {
        RepositoryInboundPort port = mock(RepositoryInboundPort.class);
        RepositoryController controller = new RepositoryController(port);

        String owner = "itba";
        String repo = "user-activity";
        int page = 2;
        int perPage = 5;

        PullRequest pr1 = mock(PullRequest.class);
        PullRequest pr2 = mock(PullRequest.class);
        List<PullRequest> expected = List.of(pr1, pr2);

        controller.setRepositoryContext(owner, repo);
        when(port.getPullRequests(owner, repo, page, perPage)).thenReturn(expected);

        var response = controller.getRepositoryPullRequests(page, perPage);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isSameAs(expected);
        verify(port).getPullRequests(eq(owner), eq(repo), eq(page), eq(perPage));
        verifyNoMoreInteractions(port);
    }

    @Test
    @DisplayName("@ModelAttribute + GET /pull-requests/merged delega a getMergedPullRequests")
    void getMergedPullRequests_ok() {
        RepositoryInboundPort port = mock(RepositoryInboundPort.class);
        RepositoryController controller = new RepositoryController(port);

        String owner = "org";
        String repo = "proj";
        int page = 1;
        int perPage = 30;

        PullRequest merged = mock(PullRequest.class);
        List<PullRequest> expected = List.of(merged);

        controller.setRepositoryContext(owner, repo);
        when(port.getMergedPullRequests(owner, repo, page, perPage)).thenReturn(expected);

        var response = controller.getMergedPullRequests(page, perPage);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isSameAs(expected);
        verify(port).getMergedPullRequests(eq(owner), eq(repo), eq(page), eq(perPage));
        verifyNoMoreInteractions(port);
    }

    @Test
    @DisplayName("@ModelAttribute + GET /commits delega a getCommits")
    void getRepositoryCommits_ok() {
        RepositoryInboundPort port = mock(RepositoryInboundPort.class);
        RepositoryController controller = new RepositoryController(port);

        String owner = "team";
        String repo = "lib";
        int page = 3;
        int perPage = 10;

        Commit c1 = mock(Commit.class);
        Commit c2 = mock(Commit.class);
        List<Commit> expected = List.of(c1, c2);

        controller.setRepositoryContext(owner, repo);
        when(port.getCommits(owner, repo, page, perPage)).thenReturn(expected);

        var response = controller.getRepositoryCommits(page, perPage);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isSameAs(expected);
        verify(port).getCommits(eq(owner), eq(repo), eq(page), eq(perPage));
        verifyNoMoreInteractions(port);
    }

    @Test
    @DisplayName("@ModelAttribute + GET /pull-requests/life-avg delega a getPullRequestsLifeAvg")
    void getPullRequestsLifeAvg_ok() {
        RepositoryInboundPort port = mock(RepositoryInboundPort.class);
        RepositoryController controller = new RepositoryController(port);

        String owner = "itba";
        String repo = "ua";

        List<PullRequestsLifeAvg> expected = List.of(
                new PullRequestsLifeAvg("2025-01", Duration.ofHours(12), 3),
                new PullRequestsLifeAvg("2025-02", Duration.ofHours(8), 2)
        );

        controller.setRepositoryContext(owner, repo);
        when(port.getPullRequestsLifeAvg(owner, repo)).thenReturn(expected);

        var response = controller.getPullRequestsLifeAvg();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isSameAs(expected);
        verify(port).getPullRequestsLifeAvg(eq(owner), eq(repo));
        verifyNoMoreInteractions(port);
    }
}
