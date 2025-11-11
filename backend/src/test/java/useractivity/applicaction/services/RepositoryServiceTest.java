package useractivity.applicaction.services;

import edu.itba.useractivity.application.services.RepositoryService;
import edu.itba.useractivity.application.usecases.GetRepositoryCommitsUseCase;
import edu.itba.useractivity.application.usecases.GetRepositoryMergedPullRequestsUseCase;
import edu.itba.useractivity.application.usecases.GetRepositoryPullRequestsLifeAvgUseCase;
import edu.itba.useractivity.application.usecases.GetRepositoryPullRequestsUseCase;
import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.models.PullRequestsLifeAvg;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RepositoryServiceTest {

    @Test
    @DisplayName("getPullRequests delega en GetRepositoryPullRequestsUseCase y devuelve su resultado")
    void getPullRequests_delegates() {
        GetRepositoryPullRequestsUseCase getPRs = mock(GetRepositoryPullRequestsUseCase.class);
        GetRepositoryCommitsUseCase getCommits = mock(GetRepositoryCommitsUseCase.class);
        GetRepositoryMergedPullRequestsUseCase getMergedPRs = mock(GetRepositoryMergedPullRequestsUseCase.class);
        GetRepositoryPullRequestsLifeAvgUseCase getLifeAvg = mock(GetRepositoryPullRequestsLifeAvgUseCase.class);

        RepositoryService service = new RepositoryService(getPRs, getCommits, getMergedPRs, getLifeAvg);

        String owner = "itba", repo = "user-activity";
        int page = 2, perPage = 5;

        PullRequest pr1 = mock(PullRequest.class), pr2 = mock(PullRequest.class);
        List<PullRequest> expected = List.of(pr1, pr2);

        when(getPRs.execute(owner, repo, page, perPage)).thenReturn(expected);

        List<PullRequest> result = service.getPullRequests(owner, repo, page, perPage);

        assertThat(result).isSameAs(expected);
        verify(getPRs).execute(eq(owner), eq(repo), eq(page), eq(perPage));
        verifyNoMoreInteractions(getPRs, getCommits, getMergedPRs, getLifeAvg);
    }

    @Test
    @DisplayName("getMergedPullRequests delega en GetRepositoryMergedPullRequestsUseCase y devuelve su resultado")
    void getMergedPullRequests_delegates() {
        GetRepositoryPullRequestsUseCase getPRs = mock(GetRepositoryPullRequestsUseCase.class);
        GetRepositoryCommitsUseCase getCommits = mock(GetRepositoryCommitsUseCase.class);
        GetRepositoryMergedPullRequestsUseCase getMergedPRs = mock(GetRepositoryMergedPullRequestsUseCase.class);
        GetRepositoryPullRequestsLifeAvgUseCase getLifeAvg = mock(GetRepositoryPullRequestsLifeAvgUseCase.class);

        RepositoryService service = new RepositoryService(getPRs, getCommits, getMergedPRs, getLifeAvg);

        String owner = "org", repo = "proj";
        int page = 1, perPage = 30;

        PullRequest merged = mock(PullRequest.class);
        List<PullRequest> expected = List.of(merged);

        when(getMergedPRs.execute(owner, repo, page, perPage)).thenReturn(expected);

        List<PullRequest> result = service.getMergedPullRequests(owner, repo, page, perPage);

        assertThat(result).isSameAs(expected);
        verify(getMergedPRs).execute(eq(owner), eq(repo), eq(page), eq(perPage));
        verifyNoMoreInteractions(getPRs, getCommits, getMergedPRs, getLifeAvg);
    }

    @Test
    @DisplayName("getCommits delega en GetRepositoryCommitsUseCase y devuelve su resultado")
    void getCommits_delegates() {
        GetRepositoryPullRequestsUseCase getPRs = mock(GetRepositoryPullRequestsUseCase.class);
        GetRepositoryCommitsUseCase getCommits = mock(GetRepositoryCommitsUseCase.class);
        GetRepositoryMergedPullRequestsUseCase getMergedPRs = mock(GetRepositoryMergedPullRequestsUseCase.class);
        GetRepositoryPullRequestsLifeAvgUseCase getLifeAvg = mock(GetRepositoryPullRequestsLifeAvgUseCase.class);

        RepositoryService service = new RepositoryService(getPRs, getCommits, getMergedPRs, getLifeAvg);

        String owner = "team", repo = "lib";
        int page = 3, perPage = 10;

        Commit c1 = mock(Commit.class), c2 = mock(Commit.class);
        List<Commit> expected = List.of(c1, c2);

        when(getCommits.execute(owner, repo, page, perPage)).thenReturn(expected);

        List<Commit> result = service.getCommits(owner, repo, page, perPage);

        assertThat(result).isSameAs(expected);
        verify(getCommits).execute(eq(owner), eq(repo), eq(page), eq(perPage));
        verifyNoMoreInteractions(getPRs, getCommits, getMergedPRs, getLifeAvg);
    }

    @Test
    @DisplayName("getPullRequestsLifeAvg delega en GetRepositoryPullRequestsLifeAvgUseCase y devuelve su resultado")
    void getPullRequestsLifeAvg_delegates() {
        GetRepositoryPullRequestsUseCase getPRs = mock(GetRepositoryPullRequestsUseCase.class);
        GetRepositoryCommitsUseCase getCommits = mock(GetRepositoryCommitsUseCase.class);
        GetRepositoryMergedPullRequestsUseCase getMergedPRs = mock(GetRepositoryMergedPullRequestsUseCase.class);
        GetRepositoryPullRequestsLifeAvgUseCase getLifeAvg = mock(GetRepositoryPullRequestsLifeAvgUseCase.class);

        RepositoryService service = new RepositoryService(getPRs, getCommits, getMergedPRs, getLifeAvg);

        String owner = "itba", repo = "protos";
        PullRequestsLifeAvg avg = mock(PullRequestsLifeAvg.class);
        List<PullRequestsLifeAvg> expected = List.of(avg);

        when(getLifeAvg.execute(owner, repo)).thenReturn(expected);

        List<PullRequestsLifeAvg> result = service.getPullRequestsLifeAvg(owner, repo);

        assertThat(result).isSameAs(expected);
        verify(getLifeAvg).execute(eq(owner), eq(repo));
        verifyNoMoreInteractions(getPRs, getCommits, getMergedPRs, getLifeAvg);
    }
}
