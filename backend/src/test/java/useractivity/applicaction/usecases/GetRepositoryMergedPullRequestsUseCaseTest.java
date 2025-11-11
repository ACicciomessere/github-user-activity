package useractivity.applicaction.usecases;


import edu.itba.useractivity.application.usecases.GetRepositoryMergedPullRequestsUseCase;
import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.ports.outbound.RepositoryOutboundPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GetRepositoryMergedPullRequestsUseCaseTest {

    @Test
    @DisplayName("execute filtra correctamente los PullRequests mergeados")
    void execute_filtersMergedPullRequests() {
        RepositoryOutboundPort repoPort = mock(RepositoryOutboundPort.class);
        GetRepositoryMergedPullRequestsUseCase useCase = new GetRepositoryMergedPullRequestsUseCase(repoPort);

        String owner = "itba", repo = "user-activity";
        int page = 1, perPage = 10;

        PullRequest merged = mock(PullRequest.class);
        PullRequest notMerged = mock(PullRequest.class);

        when(merged.isMerged()).thenReturn(true);
        when(notMerged.isMerged()).thenReturn(false);

        List<PullRequest> all = List.of(merged, notMerged);
        when(repoPort.getPullRequests(owner, repo, page, perPage)).thenReturn(all);

        // when
        List<PullRequest> result = useCase.execute(owner, repo, page, perPage);

        // then
        assertThat(result).containsExactly(merged);
        verify(repoPort).getPullRequests(owner, repo, page, perPage);
        verifyNoMoreInteractions(repoPort);
    }

    @Test
    @DisplayName("execute devuelve lista vacía si ningún PullRequest está mergeado")
    void execute_returnsEmptyIfNoneMerged() {
        RepositoryOutboundPort repoPort = mock(RepositoryOutboundPort.class);
        GetRepositoryMergedPullRequestsUseCase useCase = new GetRepositoryMergedPullRequestsUseCase(repoPort);

        PullRequest p1 = mock(PullRequest.class);
        PullRequest p2 = mock(PullRequest.class);
        when(p1.isMerged()).thenReturn(false);
        when(p2.isMerged()).thenReturn(false);

        when(repoPort.getPullRequests("o", "r", 1, 10)).thenReturn(List.of(p1, p2));

        List<PullRequest> result = useCase.execute("o", "r", 1, 10);

        assertThat(result).isEmpty();
        verify(repoPort).getPullRequests("o", "r", 1, 10);
        verifyNoMoreInteractions(repoPort);
    }
}
