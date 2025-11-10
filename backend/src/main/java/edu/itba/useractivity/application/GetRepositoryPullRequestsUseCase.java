package edu.itba.useractivity.application;

import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.ports.outbound.RepositoryDataPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRepositoryPullRequestsUseCase {
    private final RepositoryDataPort repositoryDataPort;

    public GetRepositoryPullRequestsUseCase(RepositoryDataPort repositoryDataPort) {
        this.repositoryDataPort = repositoryDataPort;
    }

    public List<PullRequest> execute(String ownerName, String repositoryName, int page, int perPage) {
        return repositoryDataPort.getPullRequests(ownerName, repositoryName, page, perPage);
    }
}
