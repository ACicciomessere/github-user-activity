package edu.itba.useractivity.domain.ports.outbound;

import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.models.PullRequest;

import java.util.List;

public interface RepositoryOutboundPort {
    List<PullRequest> getPullRequests(String ownerName, String repositoryName, int page, int perPage);
    List<Commit> getCommits(String ownerName, String repositoryName, int page, int perPage);
}
