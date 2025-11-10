package edu.itba.useractivity.application;

import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.ports.outbound.RepositoryDataPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRepositoryPullRequestsUseCase {
    private final RepositoryDataPort repositoryDataPort;

    public List<PullRequest> execute(String ownerName, String repositoryName) {
        return repositoryDataPort.getPullRequests(ownerName, repositoryName);
    }
}
