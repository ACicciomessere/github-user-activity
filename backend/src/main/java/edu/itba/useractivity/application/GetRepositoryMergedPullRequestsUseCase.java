package edu.itba.useractivity.application;

import edu.itba.useractivity.domain.models.PullRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRepositoryMergedPullRequestsUseCase {
    private final GetRepositoryPullRequestsUseCase getRepositoryPullRequestsUseCase;

    public GetRepositoryMergedPullRequestsUseCase(GetRepositoryPullRequestsUseCase getRepositoryPullRequestsUseCase) {
        this.getRepositoryPullRequestsUseCase = getRepositoryPullRequestsUseCase;
    }

    public List<PullRequest> execute(String owner, String repository) {
        return getRepositoryPullRequestsUseCase.execute(owner, repository)
                .stream()
                .filter(PullRequest::isMerged)
                .toList();
    }
}