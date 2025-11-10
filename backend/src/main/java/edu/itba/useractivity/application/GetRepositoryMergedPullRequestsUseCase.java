package edu.itba.useractivity.application;

import edu.itba.useractivity.domain.models.PullRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRepositoryMergedPullRequestsUseCase {
    private final GetRepositoryPullRequestsUseCase getRepositoryPullRequestsUseCase;

    public List<PullRequest> execute(String owner, String repository) {
        return getRepositoryPullRequestsUseCase.execute(owner, repository)
                .stream()
                .filter(PullRequest::isMerged)
                .toList();
    }
}