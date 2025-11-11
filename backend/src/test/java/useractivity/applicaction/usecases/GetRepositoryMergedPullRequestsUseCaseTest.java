package useractivity.applicaction.usecases;

import edu.itba.useractivity.application.usecases.GetRepositoryMergedPullRequestsUseCase;
import edu.itba.useractivity.application.usecases.GetRepositoryPullRequestsUseCase;
import edu.itba.useractivity.domain.models.PullRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GetRepositoryMergedPullRequestsUseCaseTest {

    @Test
    @DisplayName("execute filtra correctamente solo los PullRequests merged")
    void execute_returnsOnlyMerged() {
        GetRepositoryPullRequestsUseCase getPRs = mock(GetRepositoryPullRequestsUseCase.class);
        GetRepositoryMergedPullRequestsUseCase useCase = new GetRepositoryMergedPullRequestsUseCase(getPRs);

        String owner = "itba";
        String repo = "backend";
        int page = 1;
        int perPage = 5;

        PullRequest pr1 = mock(PullRequest.class);
        PullRequest pr2 = mock(PullRequest.class);
        PullRequest pr3 = mock(PullRequest.class);

        when(pr1.isMerged()).thenReturn(true);
        when(pr2.isMerged()).thenReturn(false);
        when(pr3.isMerged()).thenReturn(true);

        List<PullRequest> all = List.of(pr1, pr2, pr3);
        when(getPRs.execute(owner, repo, page, perPage)).thenReturn(all);

        // when
        List<PullRequest> result = useCase.execute(owner, repo, page, perPage);

        // then
        assertThat(result).containsExactly(pr1, pr3);
        verify(getPRs).execute(eq(owner), eq(repo), eq(page), eq(perPage));
        verifyNoMoreInteractions(getPRs);
    }

    @Test
    @DisplayName("execute devuelve lista vacía cuando no hay PullRequests merged")
    void execute_returnsEmptyWhenNoneMerged() {
        GetRepositoryPullRequestsUseCase getPRs = mock(GetRepositoryPullRequestsUseCase.class);
        GetRepositoryMergedPullRequestsUseCase useCase = new GetRepositoryMergedPullRequestsUseCase(getPRs);

        String owner = "team";
        String repo = "lib";
        int page = 2;
        int perPage = 10;

        PullRequest pr1 = mock(PullRequest.class);
        PullRequest pr2 = mock(PullRequest.class);
        when(pr1.isMerged()).thenReturn(false);
        when(pr2.isMerged()).thenReturn(false);

        List<PullRequest> all = List.of(pr1, pr2);
        when(getPRs.execute(owner, repo, page, perPage)).thenReturn(all);

        List<PullRequest> result = useCase.execute(owner, repo, page, perPage);

        assertThat(result).isEmpty();
        verify(getPRs).execute(eq(owner), eq(repo), eq(page), eq(perPage));
        verifyNoMoreInteractions(getPRs);
    }
}
