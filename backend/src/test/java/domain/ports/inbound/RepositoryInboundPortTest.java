package domain.ports.inbound;

import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.models.PullRequestsLifeAvg;
import edu.itba.useractivity.domain.ports.inbound.RepositoryInboundPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RepositoryInboundPortTest {

    @Test
    @DisplayName("Implementación anónima: se pueden invocar todos los métodos del contrato")
    void anonymousImplementation_executesAllMethods() {
        // dummies / mocks de modelos
        PullRequest pr1 = mock(PullRequest.class);
        PullRequest pr2 = mock(PullRequest.class);
        PullRequest prMerged = mock(PullRequest.class);
        Commit c1 = mock(Commit.class);
        Commit c2 = mock(Commit.class);
        PullRequestsLifeAvg avg = mock(PullRequestsLifeAvg.class);

        RepositoryInboundPort port = new RepositoryInboundPort() {
            @Override
            public List<PullRequest> getPullRequests(String owner, String repo, int page, int perPage) {
                return List.of(pr1, pr2);
            }

            @Override
            public List<PullRequest> getMergedPullRequests(String owner, String repo, int page, int perPage) {
                return List.of(prMerged);
            }

            @Override
            public List<Commit> getCommits(String owner, String repo, int page, int perPage) {
                return List.of(c1, c2);
            }

            @Override
            public List<PullRequestsLifeAvg> getPullRequestsLifeAvg(String owner, String repo) {
                return List.of(avg);
            }
        };

        // act
        List<PullRequest> prs = port.getPullRequests("owner", "repo", 1, 10);
        List<PullRequest> merged = port.getMergedPullRequests("owner", "repo", 1, 10);
        List<Commit> commits = port.getCommits("owner", "repo", 1, 10);
        List<PullRequestsLifeAvg> life = port.getPullRequestsLifeAvg("owner", "repo");

        // assert
        assertThat(prs).containsExactly(pr1, pr2);
        assertThat(merged).containsExactly(prMerged);
        assertThat(commits).containsExactly(c1, c2);
        assertThat(life).containsExactly(avg);
    }
}
