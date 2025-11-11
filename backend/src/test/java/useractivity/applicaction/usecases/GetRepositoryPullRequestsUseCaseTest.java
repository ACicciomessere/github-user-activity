package useractivity.applicaction.usecases;

import edu.itba.useractivity.application.usecases.GetRepositoryPullRequestsUseCase;
import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.ports.outbound.RepositoryOutboundPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GetRepositoryPullRequestsUseCaseTest {

    @Test
    @DisplayName("execute delega en repositoryDataPort.getPullRequests y devuelve su resultado")
    void execute_delegates() {
        // Arrange
        RepositoryOutboundPort repoPort = mock(RepositoryOutboundPort.class);
        GetRepositoryPullRequestsUseCase useCase = new GetRepositoryPullRequestsUseCase(repoPort);

        String owner = "itba";
        String repo = "user-activity";
        int page = 1, perPage = 10;

        PullRequest pr1 = mock(PullRequest.class);
        PullRequest pr2 = mock(PullRequest.class);
        List<PullRequest> expected = List.of(pr1, pr2);

        when(repoPort.getPullRequests(owner, repo, page, perPage)).thenReturn(expected);

        // Act
        List<PullRequest> result = useCase.execute(owner, repo, page, perPage);

        // Assert
        assertThat(result).isSameAs(expected);
        verify(repoPort).getPullRequests(eq(owner), eq(repo), eq(page), eq(perPage));
        verifyNoMoreInteractions(repoPort);
    }
}
