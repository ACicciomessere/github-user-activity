package edu.itba.useractivity.application;

import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.ports.outbound.RepositoryDataPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRepositoryCommitsUseCase {
    private final RepositoryDataPort repositoryDataPort;

    public GetRepositoryCommitsUseCase(RepositoryDataPort repositoryDataPort) {
        this.repositoryDataPort = repositoryDataPort;
    }

    public List<Commit> execute(String ownerName, String repositoryName, int page, int perPage) {
        return repositoryDataPort.getCommits(ownerName, repositoryName, page, perPage);
    }
}

