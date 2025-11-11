package domain.ports.outbound;

import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.ports.outbound.RepositoryOutboundPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RepositoryOutboundPortTest {

    @Test
    @DisplayName("Implementación anónima ejecuta correctamente ambos métodos del contrato")
    void anonymousImplementation_executesAllMethods() {
        Commit c1 = mock(Commit.class);
        Commit c2 = mock(Commit.class);
        PullRequest pr1 = mock(PullRequest.class);
        PullRequest pr2 = mock(PullRequest.class);

        RepositoryOutboundPort port = new RepositoryOutboundPort() {
            @Override
            public List<PullRequest> getPullRequests(String ownerName, String repositoryName, int page, int perPage) {
                return List.of(pr1, pr2);
            }

            @Override
            public List<Commit> getCommits(String ownerName, String repositoryName, int page, int perPage) {
                return List.of(c1, c2);
            }
        };

        List<PullRequest> prs = port.getPullRequests("itba", "user-activity", 1, 10);
        List<Commit> commits = port.getCommits("itba", "user-activity", 2, 5);

        assertThat(prs).containsExactly(pr1, pr2);
        assertThat(commits).containsExactly(c1, c2);
    }
}
