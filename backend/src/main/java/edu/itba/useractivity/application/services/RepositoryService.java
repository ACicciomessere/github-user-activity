package edu.itba.useractivity.application.services;

import edu.itba.useractivity.application.usecases.GetRepositoryCommitsUseCase;
import edu.itba.useractivity.application.usecases.GetRepositoryMergedPullRequestsUseCase;
import edu.itba.useractivity.application.usecases.GetRepositoryPullRequestsUseCase;
import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.ports.inbound.RepositoryInboundPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepositoryService implements RepositoryInboundPort {
    private final GetRepositoryPullRequestsUseCase getRepositoryPullRequestsUseCase;
    private final GetRepositoryCommitsUseCase getRepositoryCommitsUseCase;
    private final GetRepositoryMergedPullRequestsUseCase getMergedPullRequestsUseCase;

    @Override
    public List<PullRequest> getPullRequests(String owner, String repo, int page, int perPage) {
        return getRepositoryPullRequestsUseCase.execute(owner, repo, page, perPage);
    }

    @Override
    public List<PullRequest> getMergedPullRequests(String owner, String repo, int page, int perPage) {
        return getMergedPullRequestsUseCase.execute(owner, repo, page, perPage);
    }

    @Override
    public List<Commit> getCommits(String owner, String repo, int page, int perPage) {
        return getRepositoryCommitsUseCase.execute(owner, repo, page, perPage);
    }
}
