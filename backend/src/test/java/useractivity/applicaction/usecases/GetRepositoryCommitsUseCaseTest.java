package useractivity.applicaction.usecases;

import edu.itba.useractivity.application.usecases.GetRepositoryCommitsUseCase;
import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.ports.outbound.RepositoryOutboundPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GetRepositoryCommitsUseCaseTest {

    @Test
    @DisplayName("execute delega correctamente en repositoryDataPort.getCommits y devuelve su resultado")
    void execute_delegates() {
        // mocks
        RepositoryOutboundPort repoPort = mock(RepositoryOutboundPort.class);
        GetRepositoryCommitsUseCase useCase = new GetRepositoryCommitsUseCase(repoPort);

        String owner = "itba";
        String repository = "user-activity";
        int page = 2;
        int perPage = 10;

        Commit c1 = mock(Commit.class);
        Commit c2 = mock(Commit.class);
        List<Commit> expected = List.of(c1, c2);

        when(repoPort.getCommits(owner, repository, page, perPage)).thenReturn(expected);

        // when
        List<Commit> result = useCase.execute(owner, repository, page, perPage);

        // then
        assertThat(result).isSameAs(expected);
        verify(repoPort).getCommits(eq(owner), eq(repository), eq(page), eq(perPage));
        verifyNoMoreInteractions(repoPort);
    }
}
