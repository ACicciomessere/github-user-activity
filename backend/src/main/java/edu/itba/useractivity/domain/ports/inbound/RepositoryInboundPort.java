package edu.itba.useractivity.domain.ports.inbound;

import edu.itba.useractivity.domain.models.PullRequestsLifeAvg;
import edu.itba.useractivity.domain.models.CommitsResponse;
import edu.itba.useractivity.domain.models.PullRequest;

import java.util.List;

public interface RepositoryInboundPort {
    List<PullRequest> getPullRequests(String owner, String repo, int page, int perPage);
    List<PullRequest> getMergedPullRequests(String owner, String repo, int page, int perPage);
    CommitsResponse getCommits(String owner, String repo, int page, int perPage);
    List<PullRequestsLifeAvg> getPullRequestsLifeAvg(String owner, String repo);
}
