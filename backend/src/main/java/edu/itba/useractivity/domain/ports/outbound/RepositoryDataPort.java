package edu.itba.useractivity.domain.ports.outbound;

import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.models.PullRequest;

import java.util.List;

public interface RepositoryDataPort {
    List<PullRequest> getPullRequests(String ownerName, String repositoryName);
    List<Commit> getCommits(String ownerName, String repositoryName);
}
