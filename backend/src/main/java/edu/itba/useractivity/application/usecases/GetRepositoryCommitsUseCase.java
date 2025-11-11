package edu.itba.useractivity.application.usecases;

import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.ports.outbound.RepositoryOutboundPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetRepositoryCommitsUseCase {
    private final RepositoryOutboundPort repositoryDataPort;

    public List<Commit> execute(String ownerName, String repositoryName, int page, int perPage) {
        return repositoryDataPort.getCommits(ownerName, repositoryName, page, perPage);
    }
}

