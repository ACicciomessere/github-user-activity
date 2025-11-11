package edu.itba.useractivity.application.usecases;

import edu.itba.useractivity.domain.models.PullRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetRepositoryMergedPullRequestsUseCase {
    private final GetRepositoryPullRequestsUseCase getRepositoryPullRequestsUseCase;

    public List<PullRequest> execute(String owner, String repository, int page, int perPage) {
        return getRepositoryPullRequestsUseCase.execute(owner, repository, page, perPage)
                .stream()
                .filter(PullRequest::isMerged)
                .toList();
    }
}