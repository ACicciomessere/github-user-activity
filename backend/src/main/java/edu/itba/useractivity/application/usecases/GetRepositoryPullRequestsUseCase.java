package edu.itba.useractivity.application.usecases;

import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.ports.outbound.RepositoryOutboundPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetRepositoryPullRequestsUseCase {
    private final RepositoryOutboundPort repositoryDataPort;

    public List<PullRequest> execute(String ownerName, String repositoryName, int page, int perPage) {
        return repositoryDataPort.getPullRequests(ownerName, repositoryName, page, perPage);
    }
}
