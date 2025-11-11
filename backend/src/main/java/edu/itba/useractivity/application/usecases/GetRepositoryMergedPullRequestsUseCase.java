package edu.itba.useractivity.application.usecases;

import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.ports.outbound.RepositoryOutboundPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRepositoryMergedPullRequestsUseCase {
    private final RepositoryOutboundPort repositoryOutboundPort;

    public List<PullRequest> execute(String owner, String repository, int page, int perPage) {
        return repositoryOutboundPort.getPullRequests(owner, repository, page, perPage)
                .stream()
                .filter(PullRequest::isMerged)
                .toList();
    }
}